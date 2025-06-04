package school.faang.user_service.mapper.skill;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;


@Mapper(componentModel = "spring", uses = SkillMapper.class)
public interface SkillCandidateMapper {

    SkillCandidateDto toDto(SkillDto skillDto, Long offersAmount);
}