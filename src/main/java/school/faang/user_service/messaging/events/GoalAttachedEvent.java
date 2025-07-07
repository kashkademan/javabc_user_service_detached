package school.faang.user_service.messaging.events;

import java.time.LocalDateTime;

public record GoalAttachedEvent(Long userId, Long goalId, String goalTitle, LocalDateTime time) {
}
