package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/recommendation")
    public RecommendationDto create(@RequestBody @Validated CreateRecommendationDto newRecommendationDto) {
        return recommendationService.create(newRecommendationDto);
    }

    @PutMapping("/recommendation/{recommendationId}")
    public RecommendationDto update(@PathVariable("recommendationId") long recommendationId,
                                    @RequestBody @Validated UpdateRecommendationDto recommendationDto) {
        return recommendationService.update(recommendationId, recommendationDto);
    }

    @DeleteMapping("/recommendation/{recommendationId}")
    public void delete(@PathVariable("recommendationId") long recommendationId) {
        recommendationService.delete(recommendationId);
    }

    @GetMapping("/recommendation")
    public List<RecommendationDto> getByFilters(RecommendationFilterDto filters) {
        return recommendationService.getByFilters(filters);
    }
}
