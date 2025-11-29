package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RecommendationRequestServiceImpl implements RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserContext userContext;
    @Value("${recommendation.time.limit}")
    private int limit;

    @Override
    public RecommendationRequestDto create(CreateRecommendationRequestDto recommendationDto) {
        var requesterId = userContext.getUserId();
        if (requesterId == recommendationDto.receiverId()) {
            throw new DataValidationException("You can't request a recommendation from oneself");
        }

        recommendationRequestRepository.findLatestRequest(requesterId, recommendationDto.receiverId())
                .ifPresent(request -> {
                    if (request.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(limit))) {
                        throw new DataValidationException("You can't send a recommendation request more often: "
                                + limit + " months");
                    }
                });

        User requester = userRepository.getByIdOrThrow(requesterId);
        User receiver = userRepository.getByIdOrThrow(recommendationDto.receiverId());

        RecommendationRequest request = recommendationRequestMapper.toRecommendationRequest(recommendationDto);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);

        RecommendationRequest saved = recommendationRequestRepository.save(request);

        return recommendationRequestMapper.toRecommendationRequestDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public List<RecommendationRequestDto> getByFilters(RecommendationRequestFilterDto filters) {
        List<RecommendationRequest> requests = recommendationRequestRepository.findByFilters(
                filters.requesterId(),
                filters.receiverId(),
                filters.messageContains(),
                filters.status()
        );

        return requests.stream()
                .map(recommendationRequestMapper::toRecommendationRequestDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public RecommendationRequestDto getById(Long id) {
        RecommendationRequest request = recommendationRequestRepository.getByIdOrThrow(id);
        return recommendationRequestMapper.toRecommendationRequestDto(request);
    }

    @Override
    public void accept(Long id) {
        var userId = userContext.getUserId();
        RecommendationRequest request = recommendationRequestRepository.getByIdOrThrow(id);

        if (userId != request.getReceiver().getId()) {
            throw new ForbiddenException("Access denied");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new DataValidationException("Request can be accepted only if it is in PENDING status");
        }

        request.setStatus(RequestStatus.ACCEPTED);
        recommendationRequestRepository.save(request);
    }

    @Override
    public void reject(Long id, RejectionDto rejection) {
        var userId = userContext.getUserId();
        RecommendationRequest request = recommendationRequestRepository.getByIdOrThrow(id);

        if (userId != request.getReceiver().getId()) {
            throw new ForbiddenException("Access denied");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new DataValidationException("Request can be rejected only if it is in PENDING status");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.reason());
        recommendationRequestRepository.save(request);
    }
}
