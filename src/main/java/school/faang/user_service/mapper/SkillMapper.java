package school.faang.user_service.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCreateDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.RecordNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static school.faang.user_service.util.LogsConstants.NULL_OBJECT_IN_SKILL_MAPPER;

@Slf4j
@Component
public class SkillMapper {

    public Skill toSkill(SkillCreateDto dto) {
        if (dto == null) {
            log.error("SkillMapper toSkill: skillCreateDto is null");
            throw new RecordNotFoundException(NULL_OBJECT_IN_SKILL_MAPPER);
        }
        return Skill.builder()
                .title(dto.getTitle())
                .build();
    }

    public List<SkillDto> toSkillDtos(List<Skill> skills) {
        if (skills == null) {
            log.error("SkillMapper toSkillDtos: skills is null");
            throw new RecordNotFoundException(NULL_OBJECT_IN_SKILL_MAPPER);
        }
        List<SkillDto> list = new ArrayList<>(skills.size());
        skills.forEach(skill -> list.add(toDto(skill)));
        return list;
    }

    public SkillDto toDto(Skill skill) {
        if (skill == null) {
            log.error("SkillMapper toDto: skill is null");
            throw new RecordNotFoundException(NULL_OBJECT_IN_SKILL_MAPPER);
        }
        return SkillDto.builder()
                .id(skill.getId())
                .title(skill.getTitle())
                .build();
    }

    public SkillCandidateDto toCandidateDto(SkillDto skill, int count) {
        if (skill == null) {
            log.error("SkillMapper toCandidateDto: skill is null");
            throw new RecordNotFoundException(NULL_OBJECT_IN_SKILL_MAPPER);
        }
        return SkillCandidateDto.builder()
                .skill(skill)
                .offersAmount(count)
                .build();
    }

    public List<SkillCandidateDto> toOfferedDtos(List<Skill> skills) {
        Map<SkillDto, Long> skillCounts = skills.stream()
                .map(this::toDto)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        log.info("skillCounts: {}", skillCounts);

        return skillCounts.entrySet().stream()
                .map(entry -> toCandidateDto(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }
}
