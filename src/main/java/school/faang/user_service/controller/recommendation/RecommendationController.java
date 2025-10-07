package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
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
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;


@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping
    public RecommendationDto create(@Valid @RequestBody CreateRecommendationDto recommendationDto) {

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

    @PutMapping("/{recommendationId}")
    public RecommendationDto update(@PathVariable @Positive long recommendationId,
                                    @Valid @RequestBody UpdateRecommendationDto recommendationDto) {
        if (recommendationDto == null || isBlank(recommendationDto.content())) {
            throw new DataValidationException("content must not be blank");
        }
        return recommendationService.update(recommendationId, recommendationDto);
    }

    @DeleteMapping("/{recommendationId}")
    public void delete(@PathVariable long recommendationId) {
        recommendationService.delete(recommendationId);
    }

    @GetMapping
    public List<RecommendationDto> getByFilters(@Valid RecommendationFilterDto filters) {
        return recommendationService.getByFilters(filters);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
