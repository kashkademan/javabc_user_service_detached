package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class RecommendationController {
    private final RecommendationService recommendationService;

    RecommendationDto create(CreateRecommendationDto recommendationDto) {
        if (recommendationDto.receiverId() != null && !recommendationDto.content().isBlank()) {
            return recommendationService.create(recommendationDto);
        } else {
            throw new DataValidationException("Missing Receiver Id or content...");
        }
    }

    RecommendationDto update(long recommendationId, UpdateRecommendationDto recommendationDto) {
        if (!recommendationDto.content().isBlank()) {
            return recommendationService.update(recommendationId, recommendationDto);
        } else {
            throw new DataValidationException("Missing content...");
        }
    }

    void delete(long recommendationId) {
        recommendationService.delete(recommendationId);
    }

    public List<RecommendationDto> getByFilters(RecommendationFilterDto filters) {
        return recommendationService.getByFilters(filters);
    }
}
