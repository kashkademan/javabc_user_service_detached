package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import school.faang.user_service.controller.RecommendationController;
import school.faang.user_service.dto.recommendation.RecommendationDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {
    private static final Pageable PAGEABLE = PageRequest.of(0, 100, Sort.by("updated_at"));

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;

    @Test
    public void giveRecommendationTestGives() {
        RecommendationDto recommendationDto = new RecommendationDto(1L, 1L, 1L, "12", List.of(), LocalDateTime.now());
        Mockito.when(recommendationService.create(recommendationDto)).thenReturn(recommendationDto);
        assertEquals(recommendationDto, recommendationController.giveRecommendation(recommendationDto));
    }

    @Test
    public void updateRecommendationTestUpdates() {
        RecommendationDto recommendationDto = new RecommendationDto(1L, 1L, 1L, "12", List.of(), LocalDateTime.now());
        Mockito.when(recommendationService.update(recommendationDto)).thenReturn(recommendationDto);
        assertEquals(recommendationDto, recommendationController.updateRecommendation(recommendationDto));
    }

    @Test
    public void deleteRecommendationTestDeletes() {
        long id = 1;
        recommendationController.deleteRecommendation(id);
        verify(recommendationService, times(1)).delete(id);
    }

    @Test
    public void getAllUserRecommendationsTestGets() {
        long id = 1;
        Mockito.when(recommendationService.getAllUserRecommendations(id, PAGEABLE)).thenReturn(List.of());
        assertEquals(List.of(), recommendationController.getAllUserRecommendations(id, 0));
    }

    @Test
    public void getAllGivenRecommendationsTestGets() {
        long id = 1;
        Mockito.when(recommendationService.getAllGivenRecommendations(id, PAGEABLE)).thenReturn(List.of());
        assertEquals(List.of(), recommendationController.getAllGivenRecommendations(id, 0));
    }
}
