package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.entity.goal.GoalStatus;

public record GoalFilterDto(
        @NotBlank String titleContains,
        @NotBlank String descriptionContains,
        @NotNull GoalStatus status,
        @NotNull Long mentorId) {
}
