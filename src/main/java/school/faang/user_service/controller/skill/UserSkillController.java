package school.faang.user_service.controller.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.service.SkillService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserSkillController {
    private final SkillMapper skillMapper;
    private final SkillService skillService;

    @GetMapping("/{userId}/skills")
    public ResponseEntity<?> getUserSkills(@PathVariable long userId) {
        List<SkillDto> skillDtoList = skillMapper.toSkillDtos(skillService.getUserSkills(userId));
        return ResponseEntity.ok(skillDtoList);
    }

    @GetMapping("/{userId}/skills/offered")
    public ResponseEntity<?> getOfferedSkills(@PathVariable long userId) {
        List<Skill> offeredSkillList = skillService.getOfferedSkills(userId);
        List<SkillCandidateDto> offeredSkillDtoList = skillMapper.toOfferedDtos(offeredSkillList);
        return ResponseEntity.ok(offeredSkillDtoList);
    }

    @PostMapping("/{userId}/skills/offered/{skillId}")
    public ResponseEntity<?> acquireSkillFromOffers(@PathVariable long userId, @PathVariable long skillId) {
        Skill skill = skillService.acquireSkillFromOffers(userId, skillId);
        SkillDto skillDto = skillMapper.toDto(skill);
        return ResponseEntity.ok(skillDto);
    }
}

