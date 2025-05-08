package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.util.recommendation.RecommendationValidatorUtil;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendation) {
       RecommendationValidatorUtil.validate(recommendation);

       return recommendationService.create(recommendation);
    }

    @PutMapping
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto updated) {
        RecommendationValidatorUtil.validate(updated);

        return recommendationService.update(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteRecommendation(@PathVariable long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/{receiverId}")
    public ResponseEntity<List<RecommendationDto>> getAllUserRecommendations(@PathVariable long receiverId) {
        List<RecommendationDto> recommendations = recommendationService.getAllUserRecommendations(receiverId);

        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/{authorId}")
    public ResponseEntity<List<RecommendationDto>> getAllGivenRecommendations(@PathVariable long authorId) {
        List<RecommendationDto> recommendations = recommendationService.getAllGivenRecommendations(authorId);

        return ResponseEntity.ok(recommendations);
    }
}