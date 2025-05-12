package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring" )
public interface SkillMapper {
    CreateSkillDto toCreateDto(Skill skill);
    Skill toEntity(CreateSkillDto dto);

    List<SkillDto> toDtos(List<Skill> skills);
    SkillDto toDto(Skill skill);

    @Mapping(target = "skill", source = "skill")
    @Mapping(target = "offersAmount", source = "count")
    SkillCandidateDto toCandidateDto(SkillDto skill, int count);

    default List<SkillCandidateDto> toOfferedDtos(List<Skill> skills) {
        Map<SkillDto, Long> skillCounts = skills.stream()
                .map(this::toDto)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return skillCounts.entrySet().stream()
                .map(entry -> toCandidateDto(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }
}
