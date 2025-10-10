package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.user.Skill;

import java.util.List;

@Mapper
public interface SkillMapper {

    Skill toSkill(CreateSkillDto skillDto);

    SkillDto toSkillDto(Skill skill);

    List<SkillDto> toSkillsDto(List<Skill> skills);
}
