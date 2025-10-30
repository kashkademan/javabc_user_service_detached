package school.faang.user_service.dto.skill;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record CreateSkillDto(
        @NotBlank(message = "Поле title не может быть пустым.")
        @JsonProperty("title") String title
) {
}
