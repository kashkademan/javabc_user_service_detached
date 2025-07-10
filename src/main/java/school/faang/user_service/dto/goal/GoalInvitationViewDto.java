package school.faang.user_service.dto.goal;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;

/**
 * GoalInvitationDto — неизменяемая структура данных (record).
 * <p>
 * DTO для приглашения на совместную работу
 * </p>*
 *
 * @param id      идентификатор приглашения
 * @param inviter пользователь, который отправил приглашение
 * @param invited пользователь, которому отправлено приглашение
 * @param status  статус приглашения (PENDING, ACCEPTED, REJECTED)
 * @author Myrza
 * @since 07.07.2025
 */
public record GoalInvitationViewDto(
        Long id,
        UserDto inviter,
        UserDto invited,
        RequestStatus status
) {
}
