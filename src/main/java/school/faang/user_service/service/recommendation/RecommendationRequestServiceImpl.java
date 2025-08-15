package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.RecommendationRequestCreateDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestViewDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestedEvent;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.publisher.RecommendationRequestedEventPublisher;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {

    @Value("${recommendation-request.cooldown.month}")
    private int cooldownMonth;
    private final RecommendationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper mapper;
    private final List<RecommendationRequestFilter> filters;
    private final RecommendationRequestedEventPublisher publisher;
    private final UserContext userContext;

    @Override
    @Transactional
    public RecommendationRequestViewDto create(RecommendationRequestCreateDto createDto) {
        long requesterId = userContext.getUserId();
        User receiver = userRepository.getByIdOrThrow(createDto.receiverId());
        User requester = userRepository.getByIdOrThrow(requesterId);

        validate(requesterId, createDto);

        RecommendationRequest request = buildAndSaveRecommendationRequest(createDto, requester, receiver);

        log.info("Запрос рекомендации успешно создан. ID: {}", request.getId());
        publisher.publish(new RecommendationRequestedEvent(requesterId, receiver.getId(), request.getId()));
        return mapper.toViewDto(request);
    }

    @Override
    @Transactional
    public List<RecommendationRequestViewDto> getByFilters(RecommendationRequestFilterDto filtersDto) {
        Stream<RecommendationRequest> filteredRequests = requestRepository.findAll().stream();

        for (RecommendationRequestFilter filter : filters) {
            if (filter.isApplicable(filtersDto)) {
                filteredRequests = filter.apply(filteredRequests, filtersDto);
            }
        }

        log.debug("Успешно получен список отфильтрованных запросов на рекомендацию");
        return filteredRequests
                .map(mapper::toViewDto)
                .toList();
    }

    @Override
    public RecommendationRequestViewDto getById(Long id) {
        RecommendationRequest foundRequest = requestRepository.getByIdOrThrow(id);
        return mapper.toViewDto(foundRequest);
    }

    @Override
    @Transactional
    public void accept(Long id) {
        processStatusChange(
                id,
                RequestStatus.ACCEPTED,
                "Данный пользователь не может принять запрос на рекомендацию",
                "Приняты могут быть только запросы со статусом 'PENDING'"
        );
    }

    @Override
    @Transactional
    public void reject(Long id, RejectionDto rejection) {
        RecommendationRequest request = processStatusChange(
                id,
                RequestStatus.REJECTED,
                "Данный пользователь не может отклонить запрос",
                "Отклонены могут быть только запросы со статусом 'PENDING'"
        );
        request.setRejectionReason(rejection.reason());
    }

    private RecommendationRequest processStatusChange(
            Long id,
            RequestStatus newStatus,
            String receiverError,
            String statusError
    ) {
        RecommendationRequest request = requestRepository.getByIdOrThrow(id);
        Long currentUserId = userContext.getUserId();

        validateUserIsReceiver(request, currentUserId, receiverError);
        validateRequestIsPending(request, statusError);

        request.setStatus(newStatus);
        requestRepository.save(request);
        log.debug("Запрос id={} успешно обработан(статус:{}) для пользователя id={}",
                id, newStatus.getName(), currentUserId);

        return request;
    }

    private RecommendationRequest buildAndSaveRecommendationRequest(
            RecommendationRequestCreateDto dto,
            User requester,
            User receiver) {
        RecommendationRequest request = mapper.toEntity(dto);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);

        request = requestRepository.save(request);

        RecommendationRequest finalRequest = request;
        List<SkillRequest> skillRequests = dto.skillIds().stream()
                .map(skillId -> skillRequestRepository.create(finalRequest.getId(), skillId))
                .toList();
        request.setSkills(skillRequests);

        return request;
    }

    private void validate(Long requesterId, RecommendationRequestCreateDto dto) {
        validateNoSelfRecommendation(requesterId, dto.receiverId());
        validateCooldownPeriod(requesterId, dto.receiverId());
    }

    private void validateNoSelfRecommendation(Long requesterId, Long receiverId) {
        if (requesterId.equals(receiverId)) {
            log.warn("Попытка отправить запрос на рекомендацию самому себе {}", requesterId);
            throw new ForbiddenException("Пользователь не может сам себе отправить запрос на рекомендацию");
        }
    }

    private void validateCooldownPeriod(Long requesterId, Long receiverId) {
        boolean hasRecentRequest = requestRepository
                .existsByRequesterIdAndReceiverIdAndCreatedAtAfter(
                        requesterId,
                        receiverId,
                        LocalDateTime.now().minusMonths(cooldownMonth)
                );

        if (hasRecentRequest) {
            log.error("Превышен лимит отправки запросов на рекомендацию id={}", requesterId);
            throw new ForbiddenException("Запрос на рекомендацию уже был отправлен данному пользователю"
                    + "ранее " + cooldownMonth + " месяцев");
        }
    }

    private void validateUserIsReceiver(RecommendationRequest request,
                                        Long currentUserId,
                                        String errorMessage) {
        if (!currentUserId.equals(request.getReceiver().getId())) {
            log.error("Валидация получателя не пройдена: текущий пользователь (id={})"
                            + " не является получателем запроса id={} (получатель id={})",
                    currentUserId, request.getId(), request.getReceiver().getId());
            throw new ForbiddenException(errorMessage);
        }
    }

    private void validateRequestIsPending(RecommendationRequest request,
                                          String errorMessage) {
        if (request.getStatus() != (RequestStatus.PENDING)) {
            log.warn("Попытка изменить не-PENDING запрос. ID запроса: {}, текущий статус: '{}'",
                    request.getId(), request.getStatus());
            throw new ForbiddenException(errorMessage);
        }
    }
}
