package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;

/**
 * Неизменяемый DTO (Data Transfer Object) для создания нового навыка.
 * Реализован в виде record.
 * <p>
 * Используется как payload при получении данных от клиента
 * для создания нового навыка в системе.
 *
 * @param title название создаваемого навыка (обязательное поле,
 *              не может быть null или пустым, минимальная длина - 2 символа,
 *              максимальная длина - 50 символов)
 * @author JasonRon
 * @since 19.07.2025
 * @see SkillViewDto
 */
public record SkillCreateDto(@NotBlank String title) {
}
