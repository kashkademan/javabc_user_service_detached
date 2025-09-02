package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import school.faang.user_service.dto.user.UserViewDto;

import java.util.List;
/**
 * Неизменяемый DTO (Data Transfer Object), представляющий навык пользователя.
 * Реализован как record.
 * <p>
 * Используется для передачи данных о навыке между слоями приложения.
 * Содержит информацию о навыке, включая его идентификатор, название
 * и список гарантов (пользователей, подтвердивших этот навык).
 *
 * @param id уникальный идентификатор навыка (не может быть null)
 * @param title название навыка (не может быть null или пустым)
 * @param guarantors список пользователей, выступающих гарантами навыка (может быть пустым)
 * @author JasonRon
 * @since 19.07.2025
 */

public record SkillViewDto(
        @PositiveOrZero
        Long id,
        @NotNull
        String title,
        List<UserViewDto> guarantors
) {
}
