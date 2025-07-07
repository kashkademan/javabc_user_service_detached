package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SkillDto(
        @NotNull(message = "Should be present!")
        @NotBlank(message = "Should be present!")
        Long id,
        @NotNull(message = "Should be present!")
        @NotBlank(message = "Should be present!")
        String title
) {
}
