package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationController;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationFilterDto;
import school.faang.user_service.dto.recommendation.UpdateRecommendationDto;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class RecommendationControllerTest {
    @InjectMocks
    private RecommendationController recommendationController;
    @Mock
    private RecommendationService recommendationService;
    @Captor
    private ArgumentCaptor<CreateRecommendationDto> createDtoCaptor;
    @Captor
    private ArgumentCaptor<UpdateRecommendationDto> updateDtoCaptor;
    @Captor
    private ArgumentCaptor<Long> recommendationIdCaptor;
    @Captor
    private ArgumentCaptor<RecommendationFilterDto> recommendationFilterCaptor;

    @Test
    public void testCreateDto() {
        CreateRecommendationDto newRecommendationDto = new CreateRecommendationDto(1L, "content");
        RecommendationDto expectedResponse = RecommendationDto.builder().build();
        when(recommendationService.create(any())).thenReturn(expectedResponse);

        RecommendationDto actualResponse = recommendationController.create(newRecommendationDto);

        assertEquals(expectedResponse, actualResponse);
        verify(recommendationService, times(1)).create(createDtoCaptor.capture());
        assertEquals(newRecommendationDto.receiverId(), createDtoCaptor.getValue().receiverId());
        assertEquals(newRecommendationDto.content(), createDtoCaptor.getValue().content());
    }

    @Test
    public void testUpdateDto() {
        UpdateRecommendationDto recommendationDto = new UpdateRecommendationDto("updated content");
        long recommendationId = 1L;
        RecommendationDto expectedResponse = RecommendationDto.builder().build();
        when(recommendationService.update(anyLong(), any())).thenReturn(expectedResponse);

        RecommendationDto actualResponse = recommendationController.update(recommendationId, recommendationDto);

        verify(recommendationService, times(1))
                .update(recommendationIdCaptor.capture(), updateDtoCaptor.capture());
        assertEquals(expectedResponse, actualResponse);
        assertEquals(recommendationId, recommendationIdCaptor.getValue());
        assertEquals(recommendationDto.content(), updateDtoCaptor.getValue().content());
    }

    @Test
    public void testDeleteDto() {
        long id = 5L;

        recommendationController.delete(id);

        verify(recommendationService, times(1)).delete(id);
    }

    @Test
    public void testGetByFilters() {
        RecommendationFilterDto filters =
                new RecommendationFilterDto(null, 2L, null);
        List<RecommendationDto> expectedResponse = List.of(
                new RecommendationDto(1L, 2L, 3L, null,
                        LocalDateTime.of(2025, 7, 17, 3, 0)));
        when(recommendationService.getByFilters(any())).thenReturn(expectedResponse);

        List<RecommendationDto> actualResponse = recommendationController.getByFilters(filters);

        verify(recommendationService, times(1))
                .getByFilters(recommendationFilterCaptor.capture());
        assertEquals(expectedResponse, actualResponse);
        assertEquals(filters, recommendationFilterCaptor.getValue());
    }
}
