package school.faang.user_service.service.skill;

import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.user.Skill;

import java.util.List;

public interface SkillService {

    SkillDto create(CreateSkillDto skillDto);
    List<SkillDto> getByUserId(Long userId);
    List<SkillCandidateDto> getOfferedSkills(long userId);
    void acquireSkillFromOffers(long skillId, long userId);
}
