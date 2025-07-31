package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.PositiveOrZero;

/**
 * Неизменяемый DTO (Data Transfer Object), представляющий навык,
 * предложенный пользователю другими участниками системы.
 * Реализован как record.
 * <p>
 * Содержит информацию о рекомендуемом навыке и количестве предложений
 * от разных пользователей. Используется для отображения в UI списка
 * рекомендуемых пользователю навыков.
 *
 * @param skill DTO навыка, который был предложен (не может быть null)
 * @param offersAmount количество уникальных предложений этого навыка
 *                     (должно быть положительным числом)
 * @author JasonRon
 * @since 19.07.2025
 * @see SkillViewDto
 * @see SkillCreateDto
 */

public record SkillCandidateDto(
        SkillViewDto skill,
        @PositiveOrZero
        int offersAmount
) {
}
