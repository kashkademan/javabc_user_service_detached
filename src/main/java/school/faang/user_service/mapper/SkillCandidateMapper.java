package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;

@Mapper(componentModel = "spring")
public interface SkillCandidateMapper {
    SkillCandidateDto toDto(SkillDto skill, int offersAmount);
}
