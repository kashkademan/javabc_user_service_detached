package school.faang.user_service.service.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.dto.RecommendationRejectDto;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationResponseDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestControllerTest {

    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    private RecommendationRequestDto requestDto;
    private RecommendationResponseDto responseDto;
    private RecommendationRejectDto rejectDto;
    private RequestFilterDto filterDto;

    @BeforeEach
    void setUp() {
        requestDto = new RecommendationRequestDto(
                "Test title",
                "Test message",
                List.of("Java", "Spring"),
                1L,
                2L,
                null,
                null
        );

        responseDto = new RecommendationResponseDto(
                1L,
                "Test title",
                "Test message",
                List.of("Java", "Spring"),
                1L,
                2L,
                null,
                null
        );

        rejectDto = new RecommendationRejectDto("Rejection reason");
        filterDto = new RequestFilterDto(1L, null, null, null, null, null);
    }

    @Test
    void createValidRequestReturnsCreatedResponse() {
        when(recommendationRequestService.create(requestDto)).thenReturn(responseDto);

        RecommendationResponseDto result = recommendationRequestController.create(requestDto);

        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(recommendationRequestService, times(1)).create(requestDto);
    }

    @Test
    void getFilteredValidFilterReturnsFilteredRequests() {
        List<RecommendationResponseDto> expected = List.of(responseDto);
        when(recommendationRequestService.getRequests(filterDto)).thenReturn(expected);

        List<RecommendationResponseDto> result = recommendationRequestController.getFiltered(filterDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expected, result);
        verify(recommendationRequestService, times(1)).getRequests(filterDto);
    }

    @Test
    void getByIdExistingIdReturnsRequest() {
        when(recommendationRequestService.getRequest(1L)).thenReturn(responseDto);

        RecommendationResponseDto result = recommendationRequestController.getById(1L);

        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(recommendationRequestService, times(1)).getRequest(1L);
    }

    @Test
    void rejectValidRequestReturnsRejectedRequest() {
        when(recommendationRequestService.rejectRequest(1L, rejectDto)).thenReturn(responseDto);

        RecommendationResponseDto result = recommendationRequestController.reject(1L, rejectDto);

        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(recommendationRequestService, times(1)).rejectRequest(1L, rejectDto);
    }

    @Test
    void handleExceptionDataValidationExceptionReturnsBadRequest() {
        Exception exception = new DataValidationException("Test error");

        ResponseEntity<String> response = recommendationRequestController.handleException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Test error", response.getBody());
    }

    @Test
    void handleExceptionEntityNotFoundExceptionReturnsBadRequest() {
        Exception exception = new EntityNotFoundException("Not found");

        ResponseEntity<String> response = recommendationRequestController.handleException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Not found", response.getBody());
    }
}