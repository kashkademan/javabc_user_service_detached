package school.faang.user_service.controller.skill;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SkillController {

    private final SkillServiceImpl skillService;
    private final UserContext userContext;

    @PostMapping("/skill")
    public SkillDto create(@Validated CreateSkillDto skillDto) {
        return skillService.create(skillDto);
    }

    @GetMapping("/skill")
    public List<SkillDto> getByUserId(@Validated @NotNull @NotBlank Long userId) {
        return skillService.getByUserId(userId);
    }

    @GetMapping("/skill/offers")
    public List<SkillCandidateDto> getOfferedSkills() {
        return skillService.getOfferedSkills(userContext.getUserId());
    }

}
