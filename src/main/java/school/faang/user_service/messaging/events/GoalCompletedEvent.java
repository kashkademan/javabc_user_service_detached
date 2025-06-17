package school.faang.user_service.messaging.events;

import java.time.LocalDateTime;
import java.util.List;

public record GoalCompletedEvent(Long goalId, String goalTitle, List<Long> userIds, LocalDateTime time) {
}
