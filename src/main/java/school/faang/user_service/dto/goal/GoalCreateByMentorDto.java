package school.faang.user_service.dto.goal;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;
import java.util.List;

public record GoalCreateByMentorDto(@NotBlank
                                    String title,
                                    @NotBlank
                                    String description,
                                    @Nullable
                                    LocalDateTime deadline,
                                    @NotNull
                                    @NotEmpty
                                    List<@PositiveOrZero Long> userIds,
                                    @Nullable
                                    List<@PositiveOrZero Long> skillIds,
                                    @Nullable
                                    @PositiveOrZero
                                    Long parentGoalId) {
}
