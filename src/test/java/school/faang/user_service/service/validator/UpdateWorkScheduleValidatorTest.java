package school.faang.user_service.service.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.workschedule.UpdateWorkScheduleDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalTime;

public class UpdateWorkScheduleValidatorTest {
    private UpdateWorkScheduleDto validDto;
    private UpdateWorkScheduleDto invalidStartTimeDto;
    private UpdateWorkScheduleDto invalidLunchStartDto;
    private UpdateWorkScheduleDto invalidLunchEndDto;

    @BeforeEach
    void setUp() {
        validDto = new UpdateWorkScheduleDto(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                "Europe/Moscow"
        );

        invalidStartTimeDto = new UpdateWorkScheduleDto(
                LocalTime.of(14, 0),
                LocalTime.of(18, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                "Europe/Moscow"
        );

        invalidLunchStartDto = new UpdateWorkScheduleDto(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(15, 0),
                LocalTime.of(14, 0),
                "Europe/Moscow"
        );

        invalidLunchEndDto = new UpdateWorkScheduleDto(
                LocalTime.of(9, 0),
                LocalTime.of(16, 0),
                LocalTime.of(13, 0),
                LocalTime.of(17, 0),
                "Europe/Moscow"
        );
    }

    @Test
    void testAllTimesCorrect() {
        Assertions.assertDoesNotThrow(() -> UpdateWorkScheduleValidator.validate(validDto));
    }

    @Test
    void testStartTimeNotBeforeLunchStart() {
        Assertions.assertThrows(DataValidationException.class, () -> UpdateWorkScheduleValidator.validate(invalidStartTimeDto));
    }

    @Test
    void testLunchStartTimeNotBeforeLunchEnd() {
        Assertions.assertThrows(DataValidationException.class, () -> UpdateWorkScheduleValidator.validate(invalidLunchStartDto));
    }

    @Test
    void testLunchEndTimeNotBeforeEnd() {
        Assertions.assertThrows(DataValidationException.class, () -> UpdateWorkScheduleValidator.validate(invalidLunchEndDto));
    }
}



