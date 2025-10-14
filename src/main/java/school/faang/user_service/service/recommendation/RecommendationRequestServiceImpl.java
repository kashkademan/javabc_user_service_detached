package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.recommendation.RecommendationRequestSpecification;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {

    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserContext userContext;
    private final UserRepository userRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;
    @Value("${recommendation.request.timeForRequest}")
    private int timeForRequest;

    @Override
    @Transactional
    public RecommendationRequestDto create(CreateRecommendationRequestDto dto) {

        log.debug("Creating recommendation request: requesterId={}, receiverId={}, message={}",
                dto.requesterId(), dto.receiverId(), dto.message());

        if (dto.requesterId() == dto.receiverId()) {
            log.warn("Attempt to create recommendation request to self: userId={}", dto.requesterId());
            throw new DataValidationException("Requester and receiver are the same person.");
        }
        recommendationRequestRepository
                .findLatestPendingRequest(dto.requesterId(), dto.receiverId())
                .ifPresent(existingRequest -> {
                    LocalDateTime createdAt = existingRequest.getCreatedAt();
                    if (createdAt.isAfter(LocalDateTime.now().minusMonths(timeForRequest))) {
                        log.warn("Duplicate recommendation request attempt: requesterId={}, " +
                                        "receiverId={}, existingRequestId={}, createdAt={}",
                                dto.requesterId(), dto.receiverId(), existingRequest.getId(), createdAt);
                        throw new DataValidationException(
                                "You have already made a request during this period");
                    }
                });

        log.debug("Validating users existence: requesterId={}, receiverId={}", dto.requesterId(), dto.receiverId());
        User requester = userRepository.findById(dto.requesterId())
                .orElseThrow(() -> {
                    log.warn("Requester not found: requesterId={}", dto.requesterId());
                    return new ResponseStatusException(BAD_REQUEST, "Requester not found");
                });
        User receiver = userRepository.findById(dto.receiverId())
                .orElseThrow(() -> {
                    log.warn("Receiver not found: receiverId={}", dto.receiverId());
                    return new ResponseStatusException(BAD_REQUEST, "Receiver not found");
                });

        RecommendationRequest request = recommendationRequestMapper
                .toRecommendationRequest(dto);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);
        RecommendationRequest savedRequest = recommendationRequestRepository.save(request);

        return recommendationRequestMapper.toRecommendationRequestDto(savedRequest);
    }

    @Override
    public RecommendationRequestDto getById(Long id) {
        log.debug("Fetching recommendation request by id={}", id);
        RecommendationRequest request = recommendationRequestRepository.getByIdOrThrow(id);
        log.debug("Recommendation request found: id={}, status={}, requesterId={}, receiverId={}",
                request.getId(), request.getStatus(), request.getRequester().getId(), request.getReceiver().getId());
        return recommendationRequestMapper.toRecommendationRequestDto(request);
    }

    @Override
    public List<RecommendationRequestDto> getByFilters(RecommendationRequestFilterDto filterDto) {
        log.debug("Fetching recommendation requests with filters: {}", filterDto);
        Specification<RecommendationRequest> spec = RecommendationRequestSpecification.withFilters(filterDto);

        List<RecommendationRequest> entities = recommendationRequestRepository.findAll(spec);
        log.debug("Found {} recommendation requests matching filters", entities.size());
        return recommendationRequestMapper.toRecommendationRequestDtoList(entities);
    }

    @Override
    @Transactional
    public void accept(Long id) {
        log.debug("Accepting recommendation request: requestId={}, userId={}", id, userContext.getUserId());
        RecommendationRequest request = recommendationRequestRepository.getByIdOrThrow(id);
        if (!Objects.equals(userContext.getUserId(),request.getReceiver().getId())) {
            log.warn("Unauthorized accept attempt: requestId={}, userId={}, actualReceiverId={}",
                    id, userContext.getUserId(), request.getReceiver().getId());
            throw new ForbiddenException("You can not accept this request");
        }
        if (request.getStatus() != RequestStatus.PENDING) {
            log.warn("Accept attempt for non-pending request: requestId={}, currentStatus={}",
                    id, request.getStatus());
            throw new DataValidationException("Status must be pending");
        }
        request.setStatus(RequestStatus.ACCEPTED);
        recommendationRequestRepository.save(request);
        log.info("Recommendation request accepted: requestId={}, requesterId={}, receiverId={}",
                id, request.getRequester().getId(), request.getReceiver().getId());
    }

    @Override
    @Transactional
    public void reject(Long id, RejectionDto dto) {
        log.debug("Rejecting recommendation request: requestId={}, userId={}, reason={}",
                id, userContext.getUserId(), dto.reason());
        RecommendationRequest request = recommendationRequestRepository.getByIdOrThrow(id);
        if (userContext.getUserId() != request.getReceiver().getId()) {
            log.warn("Unauthorized reject attempt: requestId={}, userId={}, actualReceiverId={}",
                    id, userContext.getUserId(), request.getReceiver().getId());
            throw new ForbiddenException("You can not reject this request");
        }
        if (request.getStatus() != RequestStatus.PENDING) {
            log.warn("Reject attempt for non-pending request: requestId={}, currentStatus={}",
                    id, request.getStatus());
            throw new DataValidationException("Status must be pending");
        }
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(dto.reason());
        recommendationRequestRepository.save(request);
        log.info("Recommendation request rejected: requestId={}, requesterId={}, receiverId={}, reason={}",
                id, request.getRequester().getId(), request.getReceiver().getId(), dto.reason());
    }
}