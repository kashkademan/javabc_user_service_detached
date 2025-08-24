package school.faang.user_service.events;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GoalCompletedEvent(
        long userId,
        long goalId,
        LocalDateTime completedAt
) {
}
