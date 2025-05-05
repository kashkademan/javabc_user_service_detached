package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import school.faang.user_service.validator.Validator;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService requestService;

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {
        return requestService.create(recommendationRequest);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        return requestService.getRequests(filter);
    }

    public RecommendationRequestDto getRecommendationRequest(Long id) {
        return requestService.getRequest(id);
    }

    public RecommendationRequestDto rejectRequest(Long id, RejectionDto rejection) {
        return requestService.rejectRequest(id, rejection);
    }
}
