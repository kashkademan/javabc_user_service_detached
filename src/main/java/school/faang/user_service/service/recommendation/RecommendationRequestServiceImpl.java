package school.faang.user_service.service.recommendation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
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
public class RecommendationRequestServiceImpl implements RecommendationRequestService {

    @Value("${recommendation-request.cooldown.month}")
    private final int cooldownMonth;
    private final RecommendationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final RecommendationRequestMapper requestMapper;
    private final List<RecommendationRequestFilter> requestFilters;
    private final UserContext userContext;

    @Autowired
    public RecommendationRequestServiceImpl(
            @Value("${recommendation-request.cooldown.month}") int cooldownMonth,
            RecommendationRequestRepository requestRepository,
            UserRepository userRepository,
            SkillRequestRepository skillRequestRepository,
            RecommendationRequestMapper requestMapper,
            List<RecommendationRequestFilter> requestFilters,
            UserContext userContext) {
        this.cooldownMonth = cooldownMonth;
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.skillRequestRepository = skillRequestRepository;
        this.requestMapper = requestMapper;
        this.requestFilters = requestFilters;
        this.userContext = userContext;
    }

    @Override
    public RecommendationRequestDto create(CreateRecommendationRequestDto dto) {
        long requesterId = userContext.getUserId();
        User receiver = userRepository.getByIdOrThrow(dto.receiverId());
        User requester = userRepository.getByIdOrThrow(requesterId);

        validateCreationRules(requesterId, dto);

        RecommendationRequest request = buildRecommendationRequest(dto, requester, receiver);
        request = requestRepository.save(request);
        log.info("Recommendation request created successfully. ID: {}", request.getId());

        return requestMapper.toRecommendationRequestDto(request);
    }

    @Override
    public List<RecommendationRequestDto> getByFilters(RecommendationRequestFilterDto filtersDto) {
        Stream<RecommendationRequest> filteredRequests = requestRepository.findAll().stream();

        for (RecommendationRequestFilter filter : requestFilters) {
            if (filter.isApplicable(filtersDto)) {
                filteredRequests = filter.apply(filteredRequests, filtersDto);
            }
        }
        log.debug("Recommendation request filtering was successful");
        return filteredRequests
                .map(requestMapper::toRecommendationRequestDto)
                .toList();
    }

    @Override
    public RecommendationRequestDto getById(long id) {
        RecommendationRequest foundRequest = requestRepository.getByIdOrThrow(id);
        return requestMapper.toRecommendationRequestDto(foundRequest);
    }

    @Override
    public void accept(long id) {
        processStatusChange(
                id,
                RequestStatus.ACCEPTED,
                "This user can't accepted request",
                "Only pending requests can be accepted"
        );
    }

    @Override
    public void reject(long id, RejectionDto rejection) {
        processStatusChange(
                id,
                RequestStatus.REJECTED,
                "This user can't rejected request",
                "Only pending requests can be rejected"
        );
    }

    private void processStatusChange(
            long id,
            RequestStatus newStatus,
            String receiverError,
            String statusError
    ) {
        RecommendationRequest request = requestRepository.getByIdOrThrow(id);
        long currentUserId = userContext.getUserId();

        String statusName = newStatus.getName();
        validateUserIsRequestReceiver(request, currentUserId, receiverError);
        validateRequestIsPending(request, statusError);

        request.setStatus(newStatus);
        requestRepository.save(request);
        log.debug("Request {} successfully {} by user {}", id, statusName, currentUserId);
    }

    private RecommendationRequest buildRecommendationRequest(
            CreateRecommendationRequestDto dto,
            User requester,
            User receiver) {
        RecommendationRequest request = requestMapper.toRecommendationRequest(dto);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);
        List<SkillRequest> skillRequests = dto.skillIds().stream()
                .map(skillId -> skillRequestRepository.create(request.getId(), skillId))
                .toList();
        request.setSkills(skillRequests);
        return request;
    }

    private void validateCreationRules(long requesterId, CreateRecommendationRequestDto dto) {
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
            log.warn("Frequency limit violated for user {}", requesterId);
            throw new ForbiddenException("A recommendation request has" +
                    " already been sent to this user during the previous " + cooldownMonth + " months");
        }
    }

    private void validateUserIsRequestReceiver(RecommendationRequest request,
                                               long currentUserId,
                                               String errorMessage) {
        if (currentUserId != request.getReceiver().getId()) {
            log.warn("The user (id: {}) is not request (id: {}) receiver (id:{})",
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
