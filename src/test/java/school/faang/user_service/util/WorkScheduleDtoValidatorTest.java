package school.faang.user_service.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.WorkScheduleDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WorkScheduleDtoValidatorTest {

    WorkScheduleDtoValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new WorkScheduleDtoValidator();
    }

    @Test
    void testValidateDto_WithWrongTimeLine() {
        WorkScheduleDto workScheduleDto = WorkScheduleDto.builder()
                .id(1L)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(17, 0))
                .startLunch(LocalTime.of(12, 30))
                .endLunch(LocalTime.of(1, 0))
                .timezone("Europe/Moscow")
                .build();

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            validator.validateDto(workScheduleDto);
        });

        assertEquals("startTime should be before startLunch. " +
                        "both of them should be before endLunch. And all of them should be before endTime",
                exception.getMessage());
    }

    @Test
    void testValidateDto_HavingEmptyFields() {
        WorkScheduleDto workScheduleDto = WorkScheduleDto.builder()
                .id(1L)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(17, 0))
                .build();

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            validator.validateDto(workScheduleDto);
        });

        assertEquals("All fields should be filled", exception.getMessage());
    }

    @Test
    void testValidate_RightDto(){
        WorkScheduleDto workScheduleDto = WorkScheduleDto.builder()
                .id(1L)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(17, 0))
                .startLunch(LocalTime.of(12, 30))
                .endLunch(LocalTime.of(13, 30))
                .timezone("Europe/Moscow")
                .build();

        assertDoesNotThrow(() -> validator.validateDto(workScheduleDto));
    }
}