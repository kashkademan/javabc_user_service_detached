package school.faang.user_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @GetMapping("/userRecommendations/{receiverId}/{pageNumber}")
    public List<RecommendationDto> getAllUserRecommendations(@PathVariable long receiverId,
                                                             @PathVariable int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 100, Sort.by("updated_at").descending());
        return recommendationService.getAllUserRecommendations(receiverId, pageable);
    }

    @GetMapping("/givenRecommendations/{authorId}/{pageNumber}")
    public List<RecommendationDto> getAllGivenRecommendations(@PathVariable long authorId,
                                                              @PathVariable int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 100, Sort.by("updated_at").descending());
        return recommendationService.getAllGivenRecommendations(authorId, pageable);
    }
}
