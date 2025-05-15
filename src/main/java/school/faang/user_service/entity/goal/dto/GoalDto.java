package school.faang.user_service.entity.goal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GoalDto(
        Long id,
        String description,
        Long parentId,
        @NotBlank(message = "Empty goal title not allowed!") String title,
        GoalStatus status,
        List<Long> skillsId
) {}