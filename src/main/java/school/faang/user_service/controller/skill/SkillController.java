package school.faang.user_service.controller.skill;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Validated
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;
    private final UserContext userContext;

    @PostMapping
    public SkillDto create(@RequestBody @Valid CreateSkillDto createSkillDto) {
        return skillService.create(createSkillDto);
    }

    @GetMapping("/users/{userId}/skills")
    public List<SkillDto> getByUserId(@PathVariable long userId) {
        return skillService.getByUserId(userId);
    }

    @GetMapping("/skills/offered")
    public List<SkillCandidateDto> getOfferedSkills() {
        return skillService.getOfferedSkills(userContext.getUserId());
    }

    @PutMapping("/skills/{skillId}/acquire-from-offers")
    void acquireSkillFromOffers(@PathVariable long skillId) {
        skillService.acquireSkillFromOffers(skillId, userContext.getUserId());
    }
}
