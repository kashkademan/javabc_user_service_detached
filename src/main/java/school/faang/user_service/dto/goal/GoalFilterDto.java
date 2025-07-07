package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.Size;
import school.faang.user_service.entity.goal.GoalStatus;

public record GoalFilterDto(
        @Size(max=64, message = GoalConstant.TITLE_SIZE_NOT_VALID_MESSAGE)
        String titleContains,
        @Size(max = 4096, message = GoalConstant.DESCRIPTION_SIZE_NOT_VALID_MESSAGE)
        String descriptionContains,
        GoalStatus status,
        Long mentorId
) {
}
