package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationDto create(CreateRecommendationDto recommendationDto) {
        return recommendationService.create(recommendationDto);
    }

    public RecommendationDto update(long recommendationId, UpdateRecommendationDto recommendationDto) {
        return recommendationService.update(recommendationId, recommendationDto);
    }

    public void delete(long recommendationId) {
        recommendationService.delete(recommendationId);
    }

    public List<RecommendationDto> getByFilters(RecommendationFilterDto filters) {
        return recommendationService.getByFilters(filters);
    }
}
