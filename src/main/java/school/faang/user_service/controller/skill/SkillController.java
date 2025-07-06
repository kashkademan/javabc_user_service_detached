package school.faang.user_service.controller.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;
    private final UserContext userContext;

    public SkillDto create(CreateSkillDto skillDto) {
        validateStringNotEmpty(skillDto.title());
        return skillService.create(skillDto);
    }

   public List<SkillDto> getByUserId(Long userId){
        return skillService.getByUserId(userId);
   }

    private void validateStringNotEmpty(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Наименование навыка не может быть пустым или отсутствовать");
        }
    }

   public List<SkillCandidateDto> getOfferedSkills(){
       return skillService.getOfferedSkills(userContext.getUserId());
   }

    public void acquireSkillFromOffers(long skillId) {
        skillService.acquireSkillFromOffers(skillId, userContext.getUserId());
    }
}
