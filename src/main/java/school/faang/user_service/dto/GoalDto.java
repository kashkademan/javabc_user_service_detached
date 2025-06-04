package school.faang.user_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import school.faang.user_service.entity.goal.GoalStatus;
import java.util.List;

public record GoalDto(
        @Min(value = 1, message = "id must be a positive number")
        Long id,

        @NotBlank(message = "Field cannot be blank")
        String description,

        @Min(value = 1, message = "id must be a positive number")
        Long parentId,

        @NotBlank(message = "Field cannot be blank")
        String title,

        GoalStatus status,

        List<Long> skillIds
) {}
