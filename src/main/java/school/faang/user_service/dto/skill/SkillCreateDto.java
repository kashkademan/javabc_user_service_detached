package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static school.faang.user_service.util.LogsConstants.BLANK_SKILL_TITLE;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SkillCreateDto {
    @NotBlank(message = BLANK_SKILL_TITLE)
    private String title;
}
