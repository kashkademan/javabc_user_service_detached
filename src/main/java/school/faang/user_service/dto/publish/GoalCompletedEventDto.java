package school.faang.user_service.dto.publish;

import java.time.LocalDateTime;

public record GoalCompletedEventDto(
        long userId,
        long goalId,
        LocalDateTime timestamp) {
}
