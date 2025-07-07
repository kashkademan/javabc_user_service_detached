package school.faang.user_service.service.workschedule;

import school.faang.user_service.exception.DataValidationException;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * Интерфейс для объектов с рабочим графиком, поддерживающий валидацию данных.
 * <p>
 * Определяет необходимые методы для получения временных интервалов и часового пояса,
 * а также предоставляет дефолтный метод {@code validate()} для проверки корректности данных:
 * - последовательность времени: startTime < startLunch < endLunch < endTime,
 * - валидность часового пояса.
 * </p>
 *
 * @author agent
 * @since 07.07.2025
 */
public interface ValidatableWorkSchedule {
    LocalTime startTime();

    LocalTime startLunch();

    LocalTime endLunch();

    LocalTime endTime();

    String timezone();

    default void validate() {
        if (!startTime().isBefore(startLunch())) {
            throw new DataValidationException("Start time must be before lunch start");
        }
        if (!startLunch().isBefore(endLunch())) {
            throw new DataValidationException("Lunch start must be before lunch end");
        }
        if (!endLunch().isBefore(endTime())) {
            throw new DataValidationException("Lunch end must be before end time");
        }

        try {
            ZoneId.of(timezone());
        } catch (DateTimeException e) {
            throw new DataValidationException("Invalid timezone");
        }
    }
}