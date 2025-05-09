package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @Autowired
    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendation) {
       validateRecommendation(recommendation);

       return recommendationService.create(recommendation);
    }

    @PutMapping
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto updated) {
        validateRecommendation(updated);

        return recommendationService.update(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteRecommendation(@PathVariable long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/{authorId}")
    public ResponseEntity<List<RecommendationDto>> getAllGivenRecommendations(@PathVariable long authorId) {
        List<RecommendationDto> recommendations = recommendationService.getAllGivenRecommendations(authorId);
        return ResponseEntity.ok(recommendations);
    }

    private void validateRecommendation(RecommendationDto recommendationDto) {
        if (recommendationDto == null) {
            throw new DataValidationException("Рекомендация не может быть null");
        }
        if (recommendationDto.getAuthorId() == null) {
            throw new DataValidationException("ID автора не может быть null");
        }
        if (recommendationDto.getReceiverId() == null) {
            throw new DataValidationException("ID получателя не может быть null");
        }
        if (recommendationDto.getContent() == null || recommendationDto.getContent().isBlank()) {
            throw new DataValidationException("Текст рекомендации не может быть пустым");
        }
    }
}