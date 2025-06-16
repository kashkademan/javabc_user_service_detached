package school.faang.user_service.messaging.events;

import java.time.LocalDateTime;

public record GoalCompletedEvent(Long goalId, String goalName, LocalDateTime time) {
}
