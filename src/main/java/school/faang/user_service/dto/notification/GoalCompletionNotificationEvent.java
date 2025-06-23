package school.faang.user_service.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoalCompletionNotificationEvent implements NotificationEvent {
    String goalTitle;
}
