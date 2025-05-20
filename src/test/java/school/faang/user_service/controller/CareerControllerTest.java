package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.career.CareerController;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.service.CareerService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CareerControllerTest {
    @Mock
    CareerService careerService;
    @InjectMocks
    private CareerController careerController;

    private final Long USER_ID = 1L;
    private final Long CAREER_ID = 1L;
    private final String COMPANY = "Test Company";
    private final String POSITION = "Test Position";

    private CareerDto createTestCareerDto() {
        return CareerDto.builder()
                .id(CAREER_ID)
                .company(COMPANY)
                .position(POSITION)
                .dateFrom(LocalDate.now().minusYears(1))
                .dateTo(LocalDate.now().minusDays(5))
                .build();
    }

    @Test
    void shouldReturnsCreatedCareer_whenAddCareer() {
        CareerDto input = createTestCareerDto();
        CareerDto expected = createTestCareerDto();

        when(careerService.addCareer(USER_ID, input)).thenReturn(expected);

        CareerDto result = careerController.addCareer(USER_ID, input);

        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getCompany(), result.getCompany());
        assertEquals(expected.getPosition(), result.getPosition());

        verify(careerService).addCareer(USER_ID, input);
    }

    @Test
    void shouldReturnsUpdatedCareer_whenUpdateCareer() {
        CareerDto input = createTestCareerDto();
        CareerDto expected = createTestCareerDto();

        when(careerService.updateCareer(USER_ID, input)).thenReturn(expected);

        CareerDto result = careerController.updateCareer(USER_ID, input);

        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getCompany(), result.getCompany());

        verify(careerService).updateCareer(USER_ID, input);
    }

    @Test
    void shouldReturnCareer_whenGetById() {
        CareerDto expected = createTestCareerDto();
        when(careerService.getById(CAREER_ID)).thenReturn(expected);

        CareerDto result = careerController.getById(CAREER_ID);

        assertEquals(expected, result);

        verify(careerService).getById(CAREER_ID);
    }
}
