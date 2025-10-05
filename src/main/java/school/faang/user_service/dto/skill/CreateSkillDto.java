package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;

public record CreateSkillDto(
        @NotBlank(message = "Название не может быть пустым") String title
) {
}
