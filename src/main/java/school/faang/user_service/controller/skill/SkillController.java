package school.faang.user_service.controller.skill;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCreateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.service.skill.SkillService;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
@Slf4j
public class SkillController {
    private final SkillMapper skillMapper;
    private final SkillService skillService;

    @PostMapping
    public ResponseEntity<?> createSkill(@RequestBody @Valid @NotNull SkillCreateDto skillDto) {
        log.info("Create skill: {}", skillDto);
        Skill skill = skillMapper.toSkill(skillDto);
        skill = skillService.create(skill);
        SkillDto skillDtoCreated = skillMapper.toDto(skill);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(skillDtoCreated);
    }
}
