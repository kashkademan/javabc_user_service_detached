package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class SkillDto {
    Long id;
    @NotBlank
    String title;
}
