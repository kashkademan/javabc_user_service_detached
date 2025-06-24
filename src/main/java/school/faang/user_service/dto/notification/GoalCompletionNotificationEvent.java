package school.faang.user_service.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.user.UserDto;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoalCompletionNotificationEvent implements NotificationEvent {
    UserDto userDto;
    String goalTitle;
}
