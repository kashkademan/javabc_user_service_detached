package school.faang.user_service.controller.skill;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.SkillCreateDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillViewDto;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;
    private final UserContext userContext;

    @PostMapping
    public ResponseEntity<SkillViewDto> create(
            @Valid
            @RequestBody
            SkillCreateDto skillDto) {
        SkillViewDto created = skillService.create(skillDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SkillViewDto>> getByUserId(@PathVariable Long userId) {
        List<SkillViewDto> result = skillService.getByUserId(userId);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/offered")
    public ResponseEntity<List<SkillCandidateDto>> getOfferedSkills() {
        long currentUserId = userContext.getUserId();
        List<SkillCandidateDto> result = skillService.getOfferedSkills(currentUserId);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/acquire/{skillId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> acquireSkillFromOffers(@PathVariable Long skillId) {
        long currentUserId = userContext.getUserId();
        skillService.acquireSkillFromOffers(skillId, currentUserId);
        return ResponseEntity.ok().build();
    }
}
