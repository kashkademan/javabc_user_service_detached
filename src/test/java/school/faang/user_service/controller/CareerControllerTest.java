package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
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

    private CareerDto testCareerDto;

    @BeforeEach
    void setUp() {
        testCareerDto = CareerDto.builder()
                .id(CAREER_ID)
                .company(COMPANY)
                .position(POSITION)
                .dateFrom(LocalDate.now().minusYears(1))
                .dateTo(LocalDate.now().minusDays(5))
                .build();
    }

    @Test
    void shouldReturnCareerDto_whenAddCareer() {
        when(careerService.addCareer(USER_ID, testCareerDto)).thenReturn(testCareerDto);

        CareerDto result = careerController.addCareer(USER_ID, testCareerDto);

        assertEquals(testCareerDto, result);

        verify(careerService).addCareer(USER_ID, testCareerDto);
    }

    @Test
    void shouldReturnUpdatedCareer_whenUpdateCareer() {
        when(careerService.updateCareer(USER_ID, testCareerDto)).thenReturn(testCareerDto);

        CareerDto result = careerController.updateCareer(USER_ID, testCareerDto);

        assertEquals(testCareerDto, result);

        verify(careerService).updateCareer(USER_ID, testCareerDto);
    }

    @Test
    void shouldReturnCareer_whenGetById() {
        when(careerService.getById(CAREER_ID)).thenReturn(testCareerDto);

        CareerDto result = careerController.getById(CAREER_ID);

        assertEquals(testCareerDto, result);

        verify(careerService).getById(CAREER_ID);
    }
}
