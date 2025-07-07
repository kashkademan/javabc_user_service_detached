package school.faang.user_service.dto.goal;

import jakarta.annotation.Nullable;
import school.faang.user_service.entity.goal.GoalStatus;

public record FilterGoalDto(
        @Nullable
        String titleContains,
        @Nullable
        String descriptionContains,
        @Nullable
        GoalStatus status,
        @Nullable
        Long mentorId
) {
}
