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
        validateSkillNameNotNull(skillDto.title());
        return skillService.create(skillDto);
    }

    private void validateSkillNameNotNull(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Поле навыка пустое");
        }
    }

    public List<SkillDto> getByUserId(Long userId) {
        return skillService.getByUserId(userId);
    }

    public List<SkillCandidateDto> getOfferedSkills() {
        return skillService.getOfferedSkills(userContext.getUserId());
    }

    public void acquireSkillFromOffers(long skillId) {
        skillService.acquireSkillFromOffers(skillId, userContext.getUserId());
    }
}
