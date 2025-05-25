package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.RecommendationController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.exceptions.DataValidationException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


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
        verify(recommendationService, times(1)).create(recommendationDto);
    }

    @Test
    public void testUpdateRecommendationUpdates() {
        RecommendationDto recommendationDto = new RecommendationDto(1L, 1L, 1L, "12", List.of(), LocalDateTime.now());
        recommendationController.updateRecommendation(recommendationDto);
        verify(recommendationService, times(1)).update(recommendationDto);
    }

    @Test
    public void testDeleteRecommendationDeletes() {
        long id = 1;
        recommendationController.deleteRecommendation(id);
        verify(recommendationService, times(1)).delete(id);
    }

    @Test
    public void testGetAllUserRecommendations() {
        long id = 1;
        recommendationController.getAllUserRecommendations(id);
        verify(recommendationService, times(1)).getAllUserRecommendations(id);
    }

    @Test
    public void testAllGivenRecommendations() {
        long id = 1;
        recommendationController.getAllGivenRecommendations(id);
        verify(recommendationService, times(1)).getAllGivenRecommendations(id);
    }
}
