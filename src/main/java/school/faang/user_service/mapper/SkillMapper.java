package school.faang.user_service.mapper;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SkillMapper {

    public Skill toSkill(CreateSkillDto dto) {
        if (dto == null) {
            return null;
        }
        return Skill.builder()
                .title(dto.getTitle())
                .build();
    }

    public List<SkillDto> toSkillDtos(List<Skill> skills) {
        if (skills == null) {
            return null;
        }
        List<SkillDto> list = new ArrayList<>(skills.size());
        skills.forEach(skill -> list.add(toDto(skill)));
        return list;
    }

    public SkillDto toDto(Skill skill) {
        if (skill == null) {
            return null;
        }
        return SkillDto.builder()
                .id(skill.getId())
                .title(skill.getTitle())
                .build();
    }

    public SkillCandidateDto toCandidateDto(SkillDto skill, int count) {
        if (skill == null) {
            return null;
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

        return skillCounts.entrySet().stream()
                .map(entry -> toCandidateDto(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }
}
