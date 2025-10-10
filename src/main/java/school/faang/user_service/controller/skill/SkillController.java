package school.faang.user_service.controller.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class SkillController {
    private final SkillService skillService;
    private final UserContext userContext;

    SkillDto create(CreateSkillDto skillDto) {
        if (!skillDto.getTitle().isEmpty()) {
            skillService.create(skillDto);
        }
        return null;
    }

    List<SkillDto> getByUserId(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return skillService.getByUserId(userId);
    }

    List<SkillCandidateDto> getOfferedSkills() {
        return skillService.getOfferedSkills(userContext.getUserId());
    }
}
