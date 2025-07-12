package school.faang.user_service.service;

import java.util.List;

import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.skill.SkillOfferDto;

public interface SkillService {
    public SkillDto create(SkillDto skill);

    public List<SkillDto> getUserSkills(long userId);

    public List<SkillCandidateDto> getOfferedSkills(long userId);

    public SkillDto acquireSkillFromOffers(long skillId, long userId);

    public List<SkillOfferDto> findAllOffersOfSkill(long skillId, long userId);
}