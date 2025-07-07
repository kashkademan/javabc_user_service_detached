package school.faang.user_service.dto.workschedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
 * @author agent
 * @since 07.07.2025
 */
public record WorkScheduleCreateDto(
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime,
        @NotNull LocalTime startLunch,
        @NotNull LocalTime endLunch,
        @NotBlank String timezone
) implements ValidatableWorkSchedule {
}
