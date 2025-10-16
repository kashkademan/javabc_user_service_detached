package school.faang.user_service.controller.skill;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;
    private final UserContext userContext;

    @PostMapping
    public SkillDto create(@RequestBody CreateSkillDto dto) {
        return skillService.create(dto);
    }
    @GetMapping("/my")
    public List<SkillDto> getMySkills() {
        return skillService.getByUserId(userContext.getUserId());
    }
    @GetMapping("/offered")
    public List<SkillCandidateDto> getOfferedSkills() {
        return skillService.getOfferedSkills(userContext.getUserId());
    }
    @PostMapping("/{skillId}/acquire")
    public void acquireSkill(@PathVariable long skillId) {
        skillService.acquireSkillFromOffers(skillId, userContext.getUserId());
    }
}

