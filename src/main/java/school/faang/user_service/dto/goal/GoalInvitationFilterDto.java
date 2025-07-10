package school.faang.user_service.dto.goal;

import school.faang.user_service.entity.RequestStatus;

/**
 * DTO для фильтрации приглашений к цели.
 * <p>
 * Содержит параметры фильтрации по идентификатору пригласившего, приглашённого и статусу запроса.
 * </p>
 *
 * @param inviterId идентификатор пользователя, который отправил приглашение
 * @param invitedId идентификатор пользователя, которому отправлено приглашение
 * @param status статус приглашения (PENDING, ACCEPTED, REJECTED)
 *
 * @author Myrza
 * @since 07.07.2025
 */
public record GoalInvitationFilterDto(
        Long inviterId,
        Long invitedId,
        RequestStatus status
) {
}
