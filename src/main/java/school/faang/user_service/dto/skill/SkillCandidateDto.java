package school.faang.user_service.dto.skill;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillCandidateDto {
    private SkillDto skill;
    private long offersAmount;
}
