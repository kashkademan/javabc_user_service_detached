package school.faang.user_service.dto.goal;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;
import java.util.List;

public record GoalCreateByUserDto(@NotBlank
                                  String title,
                                  @NotBlank
                                  String description,
                                  @Nullable
                                  LocalDateTime deadline,
                                  @Nullable
                                  List<@PositiveOrZero Long> skillIds,
                                  @Nullable
                                  @PositiveOrZero
                                  Long parentGoalId) {
}
