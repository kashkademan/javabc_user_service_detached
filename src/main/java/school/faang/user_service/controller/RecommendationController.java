package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/post")
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendationDto) {
        validate(recommendationDto);
        return recommendationService.create(recommendationDto);
    }

    @PutMapping("/update")
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto recommendationDto) {
        validate(recommendationDto);

        return recommendationService.update(recommendationDto);
    }

    @DeleteMapping("/delete")
    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/userRecommendations/{receiverId}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping("/givenRecommendations/{id}")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable long id) {
        return recommendationService.getAllGivenRecommendations(id);
    }

    private void validate(RecommendationDto recommendationDto) {
        if (recommendationDto.content().isEmpty()) {
            throw new DataValidationException("Empty content");
        }
    }
}
