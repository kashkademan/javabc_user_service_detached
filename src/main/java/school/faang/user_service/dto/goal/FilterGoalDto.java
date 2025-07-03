package school.faang.user_service.dto.goal;

import school.faang.user_service.entity.goal.GoalStatus;

public record FilterGoalDto(
        String titleContains,
        String descriptionContains,
        GoalStatus status,
        Long mentorId
) {
}
