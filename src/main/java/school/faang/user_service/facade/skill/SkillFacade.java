package school.faang.user_service.facade.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillCreateRequestDto;
import school.faang.user_service.dto.skill.SkillResponseDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SkillFacade {
    private final SkillMapper skillMapper;
    private final SkillService skillService;

    public SkillResponseDto create(SkillCreateRequestDto skillCreateRequestDto) {
        Skill skill = skillMapper.toEntity(skillCreateRequestDto);
        Skill savedSkill = skillService.create(skill);
        return skillMapper.toSkillResponseDto(savedSkill);
    }

    public List<SkillResponseDto> getUserSkills() {
        List<Skill> userSkills = skillService.getUserSkills();
        return skillMapper.toSkillResponseDtoList(userSkills);
    }

    public List<SkillCandidateDto> getOfferedSkills() {
        List<Skill> offeredSkills = skillService.getOfferedSkills();
        Map<Skill, Long> offersCountBySkillId = offeredSkills.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        return offersCountBySkillId.entrySet().stream()
                .map(skillEntry -> skillMapper.toSkillCandidateDto(skillEntry.getKey(), skillEntry.getValue()))
                .toList();
    }

    public SkillResponseDto acquireSkillFromOffers(long skillId) {
        Skill acquiredSkill = skillService.acquireSkillFromOffers(skillId);
        return skillMapper.toSkillResponseDto(acquiredSkill);
    }
}
