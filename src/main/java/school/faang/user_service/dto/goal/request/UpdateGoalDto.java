package school.faang.user_service.dto.goal.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateGoalDto(
        @NotBlank(message = "Empty goal title not allowed!") String title,
        String description,
        GoalStatus status,
        @NotNull List<Long> skillsId,
        @Future(message = "Dead line must be in future!") LocalDateTime deadline
) {}