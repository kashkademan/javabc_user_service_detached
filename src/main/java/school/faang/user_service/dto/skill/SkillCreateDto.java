package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;

public record SkillCreateDto(
        @NotBlank(message = "Имя скилла не может быть пустым")
        String title
) {
}
