package school.faang.user_service.dto.goal;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;

/**
 * DTO для представления приглашения к участию в цели.
 * Содержит информацию об отправителе, получателе и статусе приглашения.
 */
public record GoalInvitationDto(
        /**
         * Уникальный идентификатор приглашения
         */
        Long id,

        /**
         * Пользователь, отправивший приглашение
         */
        UserDto inviter,

        /**
         * Пользователь, получивший приглашение
         */
        UserDto invited,

        /**
         * Текущий статус приглашения (PENDING, ACCEPTED, REJECTED)
         */
        RequestStatus status
) {}
