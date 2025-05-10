package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring" )
public interface SkillMapper {
    CreateSkillDto toDto(Skill skill);
    Skill toEntity(CreateSkillDto dto);
}
