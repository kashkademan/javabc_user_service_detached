package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;


public record CreateGoalDto(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull LocalDateTime deadline,
        @NotNull Long mentorId,
        @NotNull List<Long> userIds) {
}
