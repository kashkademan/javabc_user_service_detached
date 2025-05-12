package school.faang.user_service.dto.skill;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillCreateRequestDto {
    private Long id;
    @NotBlank(message = "Skill title is required")
    @Length(max = 64, message = "Skill title cannot exceed {max} characters")
    private String title;
}
