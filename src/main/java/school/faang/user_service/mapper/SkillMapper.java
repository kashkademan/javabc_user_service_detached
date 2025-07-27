package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.skill.SkillCreateDto;
import school.faang.user_service.dto.skill.SkillViewDto;
import school.faang.user_service.entity.user.Skill;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface SkillMapper {

    Skill toEntity(SkillCreateDto skillDto);

    SkillViewDto toViewDto(Skill skill);
}
