package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;

public record UpdateGoalDto(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull LocalDateTime deadline,
        @NotNull Long mentorId,
        @NotNull GoalStatus status) {
}
