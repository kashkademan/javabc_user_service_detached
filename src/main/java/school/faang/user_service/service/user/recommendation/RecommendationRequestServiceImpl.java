package school.faang.user_service.service.user.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.user.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.user.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.user.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestSpecification;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

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
    public RecommendationRequestDto create(CreateRecommendationRequestDto dto) {

        if (dto.requesterId() == dto.receiverId()) {
            throw new ResponseStatusException(BAD_REQUEST, "Requester and receiver are the same person.");
        }
        recommendationRequestRepository
                .findLatestPendingRequest(dto.requesterId(), dto.receiverId())
                .ifPresent(existingRequest -> {
                    LocalDateTime createdAt = existingRequest.getCreatedAt();
                    if (createdAt.isAfter(LocalDateTime.now().minusMonths(timeForRequest))) {
                        throw new ResponseStatusException(BAD_REQUEST,
                                "You have already made a request during this period");
                    }
                });

        RecommendationRequest request = recommendationRequestMapper
                .toRecommendationRequest(dto);
        request.setStatus(RequestStatus.PENDING);
        RecommendationRequest savedRequest = recommendationRequestRepository.save(request);

        return recommendationRequestMapper.toRecommendationRequestDto(savedRequest);
    }

    @Override
    public RecommendationRequestDto getById(Long id) {
        RecommendationRequest request = recommendationRequestRepository.getByIdOrThrow(id);
        return recommendationRequestMapper.toRecommendationRequestDto(request);
    }

    @Override
    public List<RecommendationRequestDto> getByFilters(RecommendationRequestFilterDto filterDto) {
        Specification<RecommendationRequest> spec = RecommendationRequestSpecification.withFilters(filterDto);

        List<RecommendationRequest> entities = recommendationRequestRepository.findAll(spec);
        return recommendationRequestMapper.toRecommendationRequestDtoList(entities);
    }

    @Override
    public void accept(Long id) {
        RecommendationRequest request = recommendationRequestRepository.getByIdOrThrow(id);
        if (userContext.getUserId() != request.getReceiver().getId()) {
            throw new IllegalArgumentException("You can not accept this request");
        }
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Status must be pending");
        }
        request.setStatus(RequestStatus.ACCEPTED);
        recommendationRequestRepository.save(request);
    }

    @Override
    public void reject(Long id, RejectionDto dto) {
        RecommendationRequest request = recommendationRequestRepository.getByIdOrThrow(id);
        if (userContext.getUserId() != request.getReceiver().getId()) {
            throw new IllegalArgumentException("You can not reject this request");
        }
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Status must be pending");
        }
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(dto.reason());
        recommendationRequestRepository.save(request);
    }
}