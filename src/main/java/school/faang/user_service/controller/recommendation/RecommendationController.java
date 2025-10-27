package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public ResponseEntity<RecommendationDto> create(@Validated @RequestBody CreateRecommendationDto recommendationDto){
        return ResponseEntity.ok(recommendationService.create(recommendationDto));
    }

    @PutMapping("/{recommendationId}")
    public ResponseEntity<RecommendationDto> update(long recommendationId, @Validated @RequestBody UpdateRecommendationDto recommendationDto){
        return ResponseEntity.ok(recommendationService.update(recommendationId,recommendationDto));
    }

    @DeleteMapping("/{recommendationId}")
    public ResponseEntity<Void> delete(long recommendationId) {
        recommendationService.delete(recommendationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/search")
    public ResponseEntity<List<RecommendationDto>> getByFilters(@Validated @RequestBody RecommendationFilterDto filters) {
        return ResponseEntity.ok(recommendationService.getByFilters(filters));
    }
}
