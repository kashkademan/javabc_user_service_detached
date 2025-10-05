package school.faang.user_service.controller.skill;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class SkillController {
    private final SkillService skillService;
    private final UserContext userContext;

    @PostMapping("/skills")
    @ResponseStatus(HttpStatus.CREATED)
    public SkillDto create(@Valid @RequestBody CreateSkillDto skillDto) {
        return skillService.create(skillDto);
    }

    @GetMapping("/users/{userId}/skills")
    @ResponseStatus(HttpStatus.OK)
    public List<SkillDto> getByUserId(
            @PathVariable
            @NotNull(message = "userId обязателен")
            @Positive(message = "userId должен быть положительным")
            @Min(value = 1, message = "userId должен быть больше 0")
            Long userId
    ) {
        return skillService.getByUserId(userId);
    }

    @GetMapping("/skills/offered")
    @ResponseStatus(HttpStatus.OK)
    public List<SkillCandidateDto> getOfferedSkills() {
        Long userId = userContext.getUserId();
        return skillService.getOfferedSkills(userId);
    }

    @PostMapping("/skills/{skillId}/acquire")
    @ResponseStatus(HttpStatus.OK)
    public void acquireSkillFromOffers(
            @PathVariable @Positive Long skillId
    ) {
        Long userId = userContext.getUserId();
        skillService.acquireSkillFromOffers(skillId, userId);
    }
}
