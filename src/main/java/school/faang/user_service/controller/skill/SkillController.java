package school.faang.user_service.controller.skill;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.SkillCreateDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.dto.skill.SkillViewDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
@Validated
public class SkillController {
    private final SkillService service;
    private final UserContext context;

    @PostMapping
    public ResponseEntity<SkillViewDto> create(@Valid @RequestBody SkillCreateDto skillDto) {
        SkillViewDto createdSkill = service.create(skillDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdSkill);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<SkillViewDto>> getByUserId(@PathVariable Long userId) {
        List<SkillViewDto> skills = service.getByUserId(userId);
        return ResponseEntity.ok(skills);
    }

    @GetMapping("/offered")
    public ResponseEntity<List<SkillOfferDto>> getOfferedSkills() {
        List<SkillOfferDto> skills = service.getOfferedSkills(context.getUserId());
        return ResponseEntity.ok(skills);
    }

    @PutMapping("/{skillId}")
    public ResponseEntity<Void> acquireSkillFromOffers(@PathVariable Long skillId) {
        service.acquireSkillFromOffers(skillId, context.getUserId());
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
