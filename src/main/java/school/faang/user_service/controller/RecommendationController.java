package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(RecommendationDto recommendationDto) {
        validate(recommendationDto);
        return recommendationService.create(recommendationDto);
    }

    public RecommendationDto updateRecommendation(RecommendationDto recommendationDto) {
        validate(recommendationDto);

        return recommendationService.update(recommendationDto);
    }

    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long id) {
        return recommendationService.getAllGivenRecommendations(id);
    }

    private void validate(RecommendationDto recommendationDto) {
        if (recommendationDto.content().isEmpty()) {
            throw new DataValidationException("Empty content");
        }
    }
}
