package school.faang.user_service.controller.recommendation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationCreateDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.RecommendationUpdateDto;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<RecommendationDto> create(
            @RequestBody
            @Valid
            RecommendationCreateDto recommendationDto) {
        RecommendationDto created = recommendationService.create(recommendationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("{recommendationId}")
    public ResponseEntity<RecommendationDto> update(
            @PathVariable
            long recommendationId,
            @RequestBody
            @Valid
            RecommendationUpdateDto recommendationDto) {
        RecommendationDto updated = recommendationService.update(recommendationId, recommendationDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{recommendationId}")
    public ResponseEntity delete(
            @PathVariable
            long recommendationId) {
        recommendationService.delete(recommendationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<RecommendationDto>> getByFilters(RecommendationFilterDto filters) {
        List<RecommendationDto> filteredRecommendation = recommendationService.getByFilters(filters);
        return ResponseEntity.ok(filteredRecommendation);
    }
}
