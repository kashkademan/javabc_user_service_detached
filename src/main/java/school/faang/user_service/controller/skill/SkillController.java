package school.faang.user_service.controller.skill;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillCreateRequestDto;
import school.faang.user_service.dto.skill.SkillResponseDto;
import school.faang.user_service.facade.skill.SkillFacade;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillFacade skillFacade;

    @PostMapping
    public ResponseEntity<SkillResponseDto> create(@RequestBody @Valid SkillCreateRequestDto skillCreateRequestDto) {
        SkillResponseDto savedSkill = skillFacade.create(skillCreateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedSkill);
    }

    @GetMapping
    public ResponseEntity<List<SkillResponseDto>> getUserSkills() {
        List<SkillResponseDto> userSkills = skillFacade.getUserSkills();
        return ResponseEntity.ok(userSkills);
    }

    @GetMapping("/candidate")
    public ResponseEntity<List<SkillCandidateDto>> getOfferedSkills() {
        List<SkillCandidateDto> offeredSkills = skillFacade.getOfferedSkills();
        return ResponseEntity.ok(offeredSkills);
    }

    @PostMapping("/{skillId}/acquire")
    public ResponseEntity<SkillResponseDto> acquireSkillFromOffers(@PathVariable long skillId) {
        SkillResponseDto acquiredSkill = skillFacade.acquireSkillFromOffers(skillId);
        return ResponseEntity.ok(acquiredSkill);
    }
}
