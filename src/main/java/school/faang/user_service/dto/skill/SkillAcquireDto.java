package school.faang.user_service.dto.skill;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SkillAcquireDto {
    private Long skillId;
    private Long userId;
}
