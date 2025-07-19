package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.user.Skill;

/*

${SkillMapper} — Маппер для преобразования между сущностями навыков и их dto.
<p>
TODO: Используется библиотека MapStruct для автоматической генерации кода преобразования:
 (toSkill) - Преобразует dto в сущность
 (toSkillDto) - Преобразует сущность в dto для передачи наружу.
</p>*
@author ${JasonRon}
@since ${19.07.2025}*/

@Mapper(componentModel = "spring")
public interface SkillMapper {
    Skill toSkill(CreateSkillDto skillDto);

    SkillDto toSkillDto(Skill skill);
}
