package school.faang.user_service.dto.goal;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;

public record UpdateGoalDto(@NotBlank String title,
                            @NotBlank String description,
                            @Nullable @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime deadline,
                            @Nullable @PositiveOrZero Long mentorId,
                            @Nullable GoalStatus status
) {
}
