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

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) throws DataValidationException {
        if (!recommendation.getContent().isEmpty()) {
            return recommendationService.create(recommendation);
        }
        throw new DataValidationException("Empty content");
    }

    public RecommendationDto updateRecommendation(RecommendationDto recommendation)  throws DataValidationException {
        if (!recommendation.getContent().isEmpty()) {
            return recommendationService.update(recommendation);
        }
        throw new DataValidationException("Empty content");
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
}
