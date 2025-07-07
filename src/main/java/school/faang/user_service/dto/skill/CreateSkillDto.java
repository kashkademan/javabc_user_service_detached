package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record CreateSkillDto(
        @NotNull(message = "Title should be present!")
        @NotBlank(message = "Title should be present!")
        String title
) {
}
