package school.faang.user_service.dto.notification;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GoalCompletionNotificationEvent implements NotificationEvent {
    String goalTitle;
}
