package school.faang.user_service.dto.goal;

import jakarta.annotation.Nullable;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateGoalDto(
        @Nullable
        String title,
        @Nullable
        String description,
        @Nullable
        LocalDateTime deadline,
        @Nullable
        Long mentorId,
        @Nullable
        GoalStatus status,
        @Nullable
        List<Long> skillIds
) {
}
