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
import school.faang.user_service.dto.recommendation.CreateRecommendationRequest;
import school.faang.user_service.dto.recommendation.RecommendationResponse;
import school.faang.user_service.dto.recommendation.FilterRecommendationRequest;
import school.faang.user_service.dto.recommendation.UpdateRecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;


@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping
    public RecommendationResponse create(@Valid @RequestBody CreateRecommendationRequest request) {

        if (request == null) {
            throw new DataValidationException("request is required");
        }
        if (request.receiverId() == null) {
            throw new DataValidationException("receiverId is required");
        }
        if (isBlank(request.content())) {
            throw new DataValidationException("content must not be blank");
        }
        return recommendationService.create(request);
    }

    @PutMapping("/{recommendationId}")
    public RecommendationResponse update(@PathVariable @Positive long recommendationId,
                                         @Valid @RequestBody UpdateRecommendationRequest request) {
        if (request == null || isBlank(request.content())) {
            throw new DataValidationException("content must not be blank");
        }
        return recommendationService.update(recommendationId, request);
    }

    @DeleteMapping("/{recommendationId}")
    public void delete(@PathVariable long recommendationId) {
        recommendationService.delete(recommendationId);
    }

    @GetMapping
    public List<RecommendationResponse> getByFilters(@Valid FilterRecommendationRequest filters) {
        return recommendationService.getByFilters(filters);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
