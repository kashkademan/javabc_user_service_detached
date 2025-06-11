package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
@RequestMapping("/skill")
@RequiredArgsConstructor
@Validated
public class SkillController {
    private final SkillService skillService;

    @Operation(
            summary = "Создание навыка",
            description = "Создает новый навык"
    )
    @PostMapping("/create")
    public SkillDto create(@RequestBody @Valid SkillDto skillDto) {
        return skillService.create(skillDto);
    }

    @Operation(
            summary = "Навыки пользователя",
            description = "Вернет список пользователей"
    )
    @GetMapping("/{userId}/get-user-skill")
    public List<SkillDto> getUserSkills(@PathVariable @Min(1) long userId) {
        return skillService.getUserSkills(userId);
    }

    @Operation(
            summary = "Предложение навыка пользователя",
            description = "Вернет список"
    )
    @GetMapping("/{userId}/get-offered-skills")
    public List<SkillCandidateDto> getOfferedSkills(@PathVariable @Min(1) long userId) {
        return skillService.getOfferedSkills(userId);
    }

    @Operation(
            summary = "Предложение навыка пользователя",
            description = "Вернет список"
    )
    @PutMapping("/{skillId}/{userId}/acquire-skill-from-offers")
    public SkillDto acquireSkillFromOffers(@PathVariable @Min(1) long skillId, @PathVariable @Min(1) long userId) {
        return skillService.acquireSkillFromOffers(skillId, userId);
    }
}