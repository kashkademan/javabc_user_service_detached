package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/recommendation")
    RecommendationDto create(@Validated CreateRecommendationDto recommendationDto) {
        return recommendationService.create(recommendationDto);
    }

    RecommendationDto update(long recommendationId, @Validated UpdateRecommendationDto recommendationDto) {
        return recommendationService.update(recommendationId, recommendationDto);
    }

    void delete(long recommendationId) {
        recommendationService.delete(recommendationId);
    }

    @GetMapping
    public List<RecommendationDto> getByFilters(RecommendationFilterDto filters) {
        return recommendationService.getByFilters(filters);
    }
}
