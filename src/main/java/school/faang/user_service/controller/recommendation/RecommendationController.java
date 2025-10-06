package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;


@Component
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationDto create(CreateRecommendationDto recommendationDto) {
        if (recommendationDto == null) {
            throw new DataValidationException("recommendationDto is required");
        }
        if (recommendationDto.receiverId() == null) {
            throw new DataValidationException("receiverId is required");
        }
        if (isBlank(recommendationDto.content())) {
            throw new DataValidationException("content must not be blank");
        }
        return recommendationService.create(recommendationDto);
    }

    public RecommendationDto update(long recommendationId, UpdateRecommendationDto recommendationDto) {
        if (recommendationDto == null || isBlank(recommendationDto.content())) {
            throw new DataValidationException("content must not be blank");
        }
        return recommendationService.update(recommendationId, recommendationDto);
    }

    public void delete(long recommendationId) {
        recommendationService.delete(recommendationId);
    }

    public List<RecommendationDto> getByFilters(RecommendationFilterDto filters) {
        return recommendationService.getByFilters(filters);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
