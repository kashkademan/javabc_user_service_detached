package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;


@Builder
public record CreateSkillDto(
        @NotNull(message = "Title should be present!")
        @NotBlank(message = "Title should be present!")
        String title
) {
}
