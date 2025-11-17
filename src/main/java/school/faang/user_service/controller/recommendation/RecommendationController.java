package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@Validated
public class RecommendationController {

    private final RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<RecommendationDto> create(@Valid @RequestBody CreateRecommendationDto recommendationDto) {
        return ResponseEntity.ok(recommendationService.create(recommendationDto));
    }

    @PutMapping("/{recommendationId}")
    public ResponseEntity<RecommendationDto> update(@PathVariable long recommendationId,
                                                    @RequestBody UpdateRecommendationDto recommendationDto) {
        return ResponseEntity.ok(recommendationService.update(recommendationId, recommendationDto));
    }

    @DeleteMapping("/{recommendationId}")
    public ResponseEntity<Void> delete(@PathVariable long recommendationId) {
        recommendationService.delete(recommendationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecommendationDto>> getByFilters(
            @RequestBody RecommendationFilterDto filters) {
        return ResponseEntity.ok(recommendationService.getByFilters(filters));
    }
}
