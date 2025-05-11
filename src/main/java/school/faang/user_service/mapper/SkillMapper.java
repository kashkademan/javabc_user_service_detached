package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import java.util.List;

@Mapper(componentModel = "spring" )
public interface SkillMapper {
    CreateSkillDto toDto(Skill skill);
    Skill toEntity(CreateSkillDto dto);

    List<SkillDto> toDtos(List<Skill> skills);
    @SuppressWarnings("unused")
    List<Skill> toEntities(List<SkillDto> dtos);
}
