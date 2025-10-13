package school.faang.user_service.service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import school.faang.user_service.dto.workschedule.WorkScheduleUpdateDto;
import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalTime;

public class WorkScheduleValidatorTest {
    private WorkScheduleDto validWorkScheduleDto;
    private WorkScheduleUpdateDto validWorkScheduleUpdateDto;
    private WorkScheduleDto invalidStartTimeDto;
    private WorkScheduleDto invalidLunchStartDto;
    private WorkScheduleDto invalidLunchEndDto;

    @BeforeEach
    void setUp() {
        validWorkScheduleDto = new WorkScheduleDto(
                1L,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                "Europe/Moscow"
        );

        validWorkScheduleUpdateDto = new WorkScheduleUpdateDto(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                "Europe/Moscow"
        );

        invalidStartTimeDto = new WorkScheduleDto(
                2L,
                LocalTime.of(14, 0),
                LocalTime.of(18, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                "Europe/Moscow"
        );

        invalidLunchStartDto = new WorkScheduleDto(
                3L,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(15, 0),
                LocalTime.of(14, 0),
                "Europe/Moscow"
        );

        invalidLunchEndDto = new WorkScheduleDto(
                4L,
                LocalTime.of(9, 0),
                LocalTime.of(16, 0),
                LocalTime.of(13, 0),
                LocalTime.of(17, 0),
                "Europe/Moscow"
        );
    }

    @Test
    void testAllTimesCorrectWithWorkScheduleDto() {
        Assertions.assertDoesNotThrow(() -> WorkScheduleValidator.validate(validWorkScheduleDto));
    }

    @Test
    void testAllTimesCorrectWithUpdateWorkScheduleDto() {
        Assertions.assertDoesNotThrow(() -> WorkScheduleValidator.validateForUpdate(validWorkScheduleUpdateDto));
    }

    @Test
    void testStartTimeNotBeforeLunchStart() {
        Assertions.assertThrows(DataValidationException.class, () ->
                WorkScheduleValidator.validate(invalidStartTimeDto));
    }

    @Test
    void testLunchStartTimeNotBeforeLunchEnd() {
        Assertions.assertThrows(DataValidationException.class, () ->
                WorkScheduleValidator.validate(invalidLunchStartDto));
    }

    @Test
    void testLunchEndTimeNotBeforeEnd() {
        Assertions.assertThrows(DataValidationException.class, () ->
                WorkScheduleValidator.validate(invalidLunchEndDto));
    }
}
