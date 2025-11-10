package school.faang.user_service.dto.event;

public record GoalCompletedEvent(
        Long userId,
        Long goalId
) {
}
