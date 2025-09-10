package school.faang.user_service.dto.goal;

public record GoalCompleteEvent(
        Long goalId,
        Long userId
) {
}