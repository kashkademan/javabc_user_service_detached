package school.faang.user_service.dto.goal;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;

public record GoalUpdateDto(@NotBlank
                            String title,
                            @NotBlank
                            String description,
                            @Nullable
                            LocalDateTime deadline,
                            @Nullable
                            @PositiveOrZero
                            Long mentorId,
                            @Nullable
                            GoalStatus status
) {
}
