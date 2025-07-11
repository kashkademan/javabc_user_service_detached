package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;

/**
 * GoalInvitationCreateDto — неизменяемая структура данных (record).
 * <p>
 * DTO для создания приглашения на совместную работу
 * </p>*
 *
 * @param invitedUserId идентификатор пользователя, которому отправлено приглашение
 * @author Myrza
 * @since 07.07.2025
 */
public record GoalInvitationCreateDto(
        @NotNull
        Long invitedUserId
) {
}
