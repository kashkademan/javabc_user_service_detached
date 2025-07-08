package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.validate.recommendation.ValidatorRecommendation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserContext userContext;
    private final List<RecommendationRequestFilter> filters;
    private final ValidatorRecommendation validatorRecommendation;

    @Override
    public RecommendationRequestDto create(CreateRecommendationRequestDto recommendationDto) {
        RecommendationRequest recommendationRequest = recommendationRequestMapper
                .toRecommendationRequest(recommendationDto);
        recommendationRequest.setRequester(userRepository.getByIdOrThrow(userContext.getUserId()));
        recommendationRequest.setReceiver(userRepository.getByIdOrThrow(recommendationDto.receiverId()));
        recommendationRequest.setStatus(RequestStatus.PENDING);
        recommendationRequest.setCreatedAt(LocalDateTime.now());
        validatorRecommendation.validateRecommendationIsRequest(recommendationRequest.getReceiver().getId(),
                userContext.getUserId(), "receiverId");
        validatorRecommendation.validateTimeOutSixMount(recommendationRequest.getCreatedAt(), "time out");
        recommendationRequest = recommendationRequestRepository.save(recommendationRequest);
        return recommendationRequestMapper.toRecommendationRequestDto(recommendationRequest);
    }

    @Override
    public List<RecommendationRequestDto> getByFilters(RecommendationRequestFilterDto filterss) {
        Stream<RecommendationRequest> allRecommendationRequest = recommendationRequestRepository.findAll().stream();
        for (RecommendationRequestFilter filter : filters) {
            if (filter.isApplicable(filterss)) {
                allRecommendationRequest = filter.apply(allRecommendationRequest, filterss);
            }
        }
        return allRecommendationRequest
                .map(recommendationRequestMapper::toRecommendationRequestDto)
                .toList();
    }

    @Override
    public RecommendationRequestDto getById(long id) {
        return recommendationRequestMapper
                .toRecommendationRequestDto(recommendationRequestRepository.getByIdOrThrow(id));
    }

    @Override
    public void accept(long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.getByIdOrThrow(id);
        validatorRecommendation.validateRecommendationToRequest(userContext.getUserId(), id, "id");
        validatorRecommendation.validateStatus(recommendationRequest.getStatus(), "Status");
        recommendationRequest.setStatus(RequestStatus.ACCEPTED);
        recommendationRequest.setUpdatedAt(LocalDateTime.now());
        recommendationRequestRepository.save(recommendationRequest);
    }

    @Override
    public void reject(long id, RejectionDto rejection) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.getByIdOrThrow(id);
        validatorRecommendation.validateRecommendationToRequest(userContext.getUserId(), id, "id");
        validatorRecommendation.validateStatus(recommendationRequest.getStatus(), "status");
        recommendationRequest.setRejectionReason(rejection.reason());
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setUpdatedAt(LocalDateTime.now());
        recommendationRequestRepository.save(recommendationRequest);
    }
}
