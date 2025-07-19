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
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
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
    private final UserContext userContext;

    @Override
    @Transactional
    public RecommendationRequestViewDto create(RecommendationRequestCreateDto createDto) {
        long requesterId = userContext.getUserId();
        User receiver = userRepository.getByIdOrThrow(createDto.receiverId());
        User requester = userRepository.getByIdOrThrow(requesterId);

        validate(requesterId, createDto);

        RecommendationRequest request = buildAndSaveRecommendationRequest(createDto, requester, receiver);

        log.info("Recommendation request created successfully. ID: {}", request.getId());
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

        log.debug("Recommendation request filtering was successful");
        return filteredRequests
                .map(mapper::toViewDto)
                .toList();
    }

    @Override
    public RecommendationRequestViewDto getById(long id) {
        RecommendationRequest foundRequest = requestRepository.getByIdOrThrow(id);
        return mapper.toViewDto(foundRequest);
    }

    @Override
    @Transactional
    public void accept(long id) {
        processStatusChange(
                id,
                RequestStatus.ACCEPTED,
                "This user can't accepted request",
                "Only pending requests can be accepted"
        );
    }

    @Override
    @Transactional
    public void reject(long id, RejectionDto rejection) {
        RecommendationRequest request = processStatusChange(
                id,
                RequestStatus.REJECTED,
                "This user can't rejected request",
                "Only pending requests can be rejected"
        );
        request.setRejectionReason(rejection.reason());
    }

    private RecommendationRequest processStatusChange(
            long id,
            RequestStatus newStatus,
            String receiverError,
            String statusError
    ) {
        RecommendationRequest request = requestRepository.getByIdOrThrow(id);
        long currentUserId = userContext.getUserId();

        validateUserIsReceiver(request, currentUserId, receiverError);
        validateRequestIsPending(request, statusError);

        request.setStatus(newStatus);
        requestRepository.save(request);
        log.debug("Request {} successfully {} by user {}", id, newStatus.getName(), currentUserId);

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

    private void validate(long requesterId, RecommendationRequestCreateDto dto) {
        validateNoSelfRecommendation(requesterId, dto.receiverId());
        validateCooldownPeriod(requesterId, dto.receiverId());
    }

    private void validateNoSelfRecommendation(long requesterId, long receiverId) {
        if (requesterId == receiverId) {
            log.warn("Self-recommendation attempt by user {}", requesterId);
            throw new ForbiddenException("The user cannot send a request to himself");
        }
    }

    private void validateCooldownPeriod(long requesterId, long receiverId) {
        boolean hasRecentRequest = requestRepository
                .existsByRequesterIdAndReceiverIdAndCreatedAtAfter(
                        requesterId,
                        receiverId,
                        LocalDateTime.now().minusMonths(cooldownMonth)
                );

        if (hasRecentRequest) {
            log.error("Frequency limit violated for user {}", requesterId);
            throw new ForbiddenException("A recommendation request has"
                    + " already been sent to this user during the previous " + cooldownMonth + " months");
        }
    }


    private void validateUserIsReceiver(RecommendationRequest request,
                                        long currentUserId,
                                        String errorMessage) {
        if (currentUserId != request.getReceiver().getId()) {
            log.error("The user (id: {}) is not request (id: {}) receiver (id:{})",
                    currentUserId, request.getId(), request.getReceiver().getId());
            throw new ForbiddenException(errorMessage);
        }
    }

    private void validateRequestIsPending(RecommendationRequest request,
                                          String errorMessage) {
        if (request.getStatus() != (RequestStatus.PENDING)) {
            log.warn("Invalid request (id:{}) state: Current status is {}",
                    request.getId(), request.getStatus());
            throw new ForbiddenException(errorMessage);
        }
    }
}
