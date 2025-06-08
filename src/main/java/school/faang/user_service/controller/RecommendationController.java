package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping()
    public RecommendationDto giveRecommendation(@RequestBody @Valid RecommendationDto recommendationDto) {
        return recommendationService.create(recommendationDto);
    }

    @PutMapping()
    public RecommendationDto updateRecommendation(@RequestBody @Valid RecommendationDto recommendationDto) {
        return recommendationService.update(recommendationDto);
    }

    @DeleteMapping()
    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    @GetMapping("/userRecommendations/{receiverId}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long receiverId,
                                                             @RequestParam("page") int page) {
        return recommendationService.getAllUserRecommendations(receiverId, page);
    }

    @GetMapping("/givenRecommendations/{authorId}/{pageNumber}")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable long authorId,
                                                              @RequestParam("page") int page) {
        return recommendationService.getAllGivenRecommendations(authorId, page);
    }
}
