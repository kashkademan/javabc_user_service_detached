package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CareerValidatorTest {
    private final CareerValidator careerValidator = new CareerValidator();

    private CareerDto createValidCareerDto() {
        return CareerDto.builder()
                .dateFrom(LocalDate.now().minusYears(1))
                .dateTo(LocalDate.now().minusDays(5))
                .company("Test Company")
                .position("Test Position")
                .build();
    }

    @Test
    void testValidateDate_whenDateFromInFuture_thenThrowsException() {
        CareerDto careerDto = createValidCareerDto();
        careerDto.setDateFrom(LocalDate.now().plusDays(1));

        assertThrows(DataValidationException.class, () -> careerValidator.validateDate(careerDto));
    }

    @Test
    void testValidateDate_whenDateToInFuture_thenThrowsException() {
        CareerDto careerDto = createValidCareerDto();
        careerDto.setDateTo(LocalDate.now().plusDays(1));

        assertThrows(DataValidationException.class, () -> careerValidator.validateDate(careerDto));
    }

    @Test
    void testValidateDate_whenDateToBeforeDateFrom_thenThrowsException() {
        CareerDto careerDto = createValidCareerDto();
        careerDto.setDateTo(LocalDate.now().minusYears(2));

        assertThrows(DataValidationException.class, () -> careerValidator.validateDate(careerDto));
    }

    @Test
    void shouldThrowException_whenDateFromIsNull() {
        CareerDto dto = createValidCareerDto();
        dto.setDateFrom(null);

        assertThrows(DataValidationException.class,
                () -> careerValidator.validateDate(dto));
    }

    @Test
    void testValidateDate_whenDateToIsNull_thenNoException() {
        CareerDto dto = createValidCareerDto();
        dto.setDateTo(null);

        assertDoesNotThrow(() -> careerValidator.validateDate(dto));
    }
}
