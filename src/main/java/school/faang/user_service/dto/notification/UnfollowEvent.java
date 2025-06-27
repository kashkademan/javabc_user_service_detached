package school.faang.user_service.dto.notification;

import lombok.Builder;
import school.faang.user_service.dto.user.UserDto;

@Builder
public record UnfollowEvent(
        UserDto owner,
        UserDto follower
) implements NotificationEvent {}