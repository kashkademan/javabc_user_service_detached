package school.faang.user_service.entity.goal.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateGoalDto(
        @NotBlank(message = "Empty goal title not allowed!") String title,
        String description,
        Long parentId,
        @NotNull List<Long> skillsId,
        @Future(message = "Deadline must be in future!") LocalDateTime deadline
) {}