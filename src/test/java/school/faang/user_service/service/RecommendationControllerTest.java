package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.RecommendationController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exceptions.DataValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {
    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;

    @Test
    public void testEmptyContent() {
        RecommendationDto recommendationDto = new RecommendationDto(1L, 1L, 1L, "", List.of(), LocalDateTime.now());
        assertThrows(DataValidationException.class,
                () -> recommendationController.giveRecommendation(recommendationDto));
    }

    @Test
    public void testGiveRecommendationGives() {
        RecommendationDto recommendationDto = new RecommendationDto(1L, 1L, 1L, "12", List.of(), LocalDateTime.now());
        recommendationController.giveRecommendation(recommendationDto);
        Mockito.verify(recommendationService).create(recommendationDto);
    }

    @Test
    public void testUpdateRecommendationUpdates() {
        RecommendationDto recommendationDto = new RecommendationDto(1L, 1L, 1L, "12", List.of(), LocalDateTime.now());
        recommendationController.updateRecommendation(recommendationDto);
        Mockito.verify(recommendationService).update(recommendationDto);
    }

    @Test
    public void testDeleteRecommendationDeletes() {
        long id = 1;
        recommendationController.deleteRecommendation(id);
        Mockito.verify(recommendationService).delete(id);
    }

    @Test
    public void testGetAllUserRecommendations() {
        long id = 1;
        recommendationController.getAllUserRecommendations(id);
        Mockito.verify(recommendationService).getAllUserRecommendations(id);
    }

    @Test
    public void testAllGivenRecommendations() {
        long id = 1;
        recommendationController.getAllGivenRecommendations(id);
        Mockito.verify(recommendationService).getAllGivenRecommendations(id);
    }
}
