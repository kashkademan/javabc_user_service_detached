package school.faang.user_service.dto;

import jakarta.validation.constraints.NotNull;
import school.faang.user_service.entity.goal.GoalStatus;
import java.time.LocalDateTime;

public record GoalFilterDto(
        @NotNull(message = "Field cannot be null")
        String title,

        @NotNull(message = "Field cannot be null")
        GoalStatus status,


        @NotNull(message = "Field cannot be null")
        LocalDateTime deadline,

        @NotNull(message = "Field cannot be null")
        Long mentorId,

        @NotNull(message = "Field cannot be null")
        LocalDateTime createdAt,

        @NotNull(message = "Field cannot be null")
        LocalDateTime updatedAt
) {}
