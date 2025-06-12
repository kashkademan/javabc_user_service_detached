package school.faang.user_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateGoalRequestDto(
        @Min(value = 1, message = "id must be a positive number")
        Long userId,

        @Min(value = 1, message = "id must be a positive number")
        Long parentId,

        @NotBlank(message = "Field cannot be blank")
        String title,

        String description,
        List<Long> skillIds
) {}
