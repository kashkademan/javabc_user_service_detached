package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSkillDto(
        @NotNull(message = "Не может быть пустым")
        @NotBlank(message = "Не может быть пустым")
        String title
) {
}
