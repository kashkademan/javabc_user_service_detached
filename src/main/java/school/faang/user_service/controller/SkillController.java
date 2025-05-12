package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.service.SkillService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
@Slf4j
public class SkillController {
    private final SkillMapper skillMapper;
    private final SkillService skillService;

    @PostMapping
    public ResponseEntity<?> createSkill(@RequestBody CreateSkillDto skillDto) {
        try {
            Skill skill = skillMapper.toEntity(skillDto);
            skill = skillService.create(skill);
            CreateSkillDto skillDtoCreated = skillMapper.toCreateDto(skill);
            log.info("Созданный навык: {}", skillDtoCreated);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(skillDtoCreated);
        } catch (DataValidationException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserSkills() {
        List<SkillDto> skillDtoList = skillMapper.toDtos(skillService.getUserSkills());
        log.info("Полученные навыки {}", skillDtoList);
        return ResponseEntity.ok(skillDtoList);
    }

    @GetMapping("/offered")
    public ResponseEntity<?> getOfferedSkills() {
        List<SkillCandidateDto> offeredSkillDtoList = skillMapper.toOfferedDtos(skillService.getOfferedSkills());
        log.info("Предложенные навыки {}", offeredSkillDtoList);
        return ResponseEntity.ok(offeredSkillDtoList);
    }

}
