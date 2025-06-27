package school.faang.user_service.dto.notification;

import lombok.Builder;
import school.faang.user_service.dto.user.UserDto;

@Builder
public record NewFollowerEvent (
        UserDto owner,
        UserDto follower
) implements NotificationEvent {}