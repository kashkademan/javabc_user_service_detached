package school.faang.user_service.controller.skill;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService service;
    private final UserContext context;
    private final SkillControllerValidator controllerValidator;

    @PostMapping
    public ResponseEntity<SkillDto> create(@Valid @RequestBody CreateSkillDto skillDto) {
        controllerValidator.validationParameters(skillDto);
        SkillDto createdSkill = service.create(skillDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdSkill);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SkillDto>> getByUserId(@PathVariable Long userId) {
        controllerValidator.validationParameters(userId);
        List<SkillDto> skills = service.getByUserId(userId);
        return skills.isEmpty()
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.ok(skills);
    }

    @GetMapping("/offered")
    public ResponseEntity<List<SkillCandidateDto>> getOfferedSkills() {
        List<SkillCandidateDto> skills = service.getOfferedSkills(context.getUserId());
        return skills.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(skills);
    }

    @PutMapping("/acquire")
    public ResponseEntity<Void> acquireSkillFromOffers(@RequestParam Long skillId) {
        controllerValidator.validationParameters(skillId);
        service.acquireSkillFromOffers(skillId, context.getUserId());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
