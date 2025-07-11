package school.faang.user_service.dto.workschedule;

import school.faang.user_service.service.workschedule.ValidatableWorkSchedule;

import java.time.LocalTime;

/**
 * DTO для создания рабочего графика пользователя.
 * <p>
 * Содержит поля, необходимые для задания временных интервалов рабочего дня и обеда,
 * а также часового пояса. Используется на этапе создания графика.
 * </p>
 *
 * <p>
 * Класс реализует интерфейс {@link ValidatableWorkSchedule} для проверки
 * бизнес-ограничений (например, корректного порядка времён).
 * </p>
 *
 * @param startTime   время начала рабочего дня
 * @param endTime     время окончания рабочего дня
 * @param startLunch  время начала обеда
 * @param endLunch    время окончания обеда
 * @param timezone    строковое представление часового пояса (не пустое)
 */
public record WorkScheduleViewDto(
        long id,
        LocalTime startTime,
        LocalTime endTime,
        LocalTime startLunch,
        LocalTime endLunch,
        String timezone
) {
}