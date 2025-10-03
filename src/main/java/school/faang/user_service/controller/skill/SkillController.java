package school.faang.user_service.controller.skill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@Slf4j
@Component
public class SkillController {
    private final SkillService skillService;
    private final UserContext userContext;

    @Autowired
    public SkillController(SkillService skillService, UserContext userContext) {
        this.skillService = skillService;
        this.userContext = userContext;
    }

    public SkillDto create(CreateSkillDto skillDto) {
        if (skillDto.title() == null || skillDto.title().isBlank()) {
            throw new DataValidationException("Поле title не может быть null или пустым");
        }

        return skillService.create(skillDto);
    }

    public List<SkillDto> getByUserId(Long userId) {
        if (userId == null || userId < 0) {
            throw new DataValidationException("ID пользователя не может быть null или меньше нуля");
        }

        if (!userId.equals(userContext.getUserId())) {
            throw new DataValidationException("ID пользователя не совпадает с ID текущего пользователя");
        }

        return skillService.getByUserId(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills() {
        long currentUserId = userContext.getUserId();
        return skillService.getOfferedSkills(currentUserId);
    }

    public void acquireSkillFromOffers(long skillId) {
        long currentUserId = userContext.getUserId();
        skillService.acquireSkillFromOffers(skillId, currentUserId);
    }


}
