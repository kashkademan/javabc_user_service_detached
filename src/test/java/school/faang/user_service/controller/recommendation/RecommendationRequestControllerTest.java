package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestControllerTest {

    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    @Test
    @DisplayName("Create recommendation request with validate string is null")
    void testCreateWithValidateStringIsNull() {
        CreateRecommendationRequestDto recommendationDto = new CreateRecommendationRequestDto(
                1L,
                null,
                null);

        assertThrows(DataValidationException.class, () -> recommendationRequestController.create(recommendationDto));
    }

    @Test
    @DisplayName("Create recommendation request with validate string is empty")
    void testCreateWithValidateStringIsEmpty() {
        CreateRecommendationRequestDto recommendationDto = new CreateRecommendationRequestDto(
                1L,
                "",
                null);

        assertThrows(DataValidationException.class, () -> recommendationRequestController.create(recommendationDto));
    }

    @Test
    @DisplayName("Create recommendation request with validate string is blank")
    void testCreateWithValidateStringIsBlank() {
        CreateRecommendationRequestDto recommendationDto = new CreateRecommendationRequestDto(
                1L,
                "  ",
                null);

        assertThrows(DataValidationException.class, () -> recommendationRequestController.create(recommendationDto));
    }

    @Test
    @DisplayName("Create recommendation request with validate receiverId is null")
    void testCreateWithValidateReceiverIdIsNull() {
        CreateRecommendationRequestDto recommendationDto = new CreateRecommendationRequestDto(
                null,
                "some message",
                null);

        assertThrows(DataValidationException.class, () -> recommendationRequestController.create(recommendationDto));
    }

    @Test
    @DisplayName("Create recommendation request with valid data")
    void testCreateWithValidData() {
        CreateRecommendationRequestDto createRecommendationRequestDto = new CreateRecommendationRequestDto(
                3L,
                "some message",
                null);

        recommendationRequestController.create(createRecommendationRequestDto);

        verify(recommendationRequestService, times(1))
                .create(createRecommendationRequestDto);
    }

    @Test
    @DisplayName("Get recommendation request by filters with requesterId is null")
    void testGetByFiltersWithRequesterIdIsNull() {
        RecommendationRequestFilterDto filters = new RecommendationRequestFilterDto(
                null,
                null,
                null,
                null);

        assertThrows(DataValidationException.class, () -> recommendationRequestController.getByFilters(filters));

    }

    @Test
    @DisplayName("Get recommendation request by filters with valid data")
    void testGetByFiltersWithValidData() {
        RecommendationRequestFilterDto filters = new RecommendationRequestFilterDto(
                1L,
                null,
                null,
                null);

        recommendationRequestController.getByFilters(filters);

        verify(recommendationRequestService, times(1))
                .getByFilters(filters);
    }

    @Test
    @DisplayName("Get recommendation request by id")
    void testGetById() {
        long recommendationRequestId = 1L;

        recommendationRequestController.getById(recommendationRequestId);

        verify(recommendationRequestService, times(1))
                .getById(recommendationRequestId);
    }

    @Test
    @DisplayName("Accept recommendation request")
    void testAccept() {
        long recommendationRequestId = 2L;

        recommendationRequestController.accept(recommendationRequestId);

        verify(recommendationRequestService, times(1))
                .accept(recommendationRequestId);
    }

    @Test
    @DisplayName("Reject recommendation request with validate string is null")
    void testRejectWithValidateStringIsNull() {
        long recommendationRequestId = 3L;
        RejectionDto rejection = new RejectionDto(null);

        assertThrows(DataValidationException.class, () ->
                recommendationRequestController.reject(recommendationRequestId, rejection));
    }

    @Test
    @DisplayName("Reject recommendation request with validate string is empty")
    void testRejectWithValidateStringIsEmpty() {
        long recommendationRequestId = 4L;
        RejectionDto rejection = new RejectionDto("");

        assertThrows(DataValidationException.class, () ->
                recommendationRequestController.reject(recommendationRequestId, rejection));
    }

    @Test
    @DisplayName("Reject recommendation request with validate string is blank")
    void testRejectWithValidateStringIsBlank() {
        long recommendationRequestId = 5L;
        RejectionDto rejection = new RejectionDto("  ");

        assertThrows(DataValidationException.class, () ->
                recommendationRequestController.reject(recommendationRequestId, rejection));
    }

    @Test
    @DisplayName("Reject recommendation request with valid data")
    void testRejectWithValidData() {
        long recommendationRequestId = 6L;
        RejectionDto rejection = new RejectionDto("some reason");

        recommendationRequestController.reject(recommendationRequestId, rejection);

        verify(recommendationRequestService, times(1))
                .reject(recommendationRequestId, rejection);
    }
}
