package school.faang.user_service.controller.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
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
        log.info("У пользователя {} полученные навыки {}", userId, skillDtoList);
        return ResponseEntity.ok(skillDtoList);
    }

    @GetMapping("/{userId}/skills/offered")
    public ResponseEntity<?> getOfferedSkills(@PathVariable long userId) {
        List<SkillCandidateDto> offeredSkillDtoList = skillMapper.toOfferedDtos(skillService.getOfferedSkills(userId));
        log.info("У пользователя {} предложенные навыки {}", userId, offeredSkillDtoList);
        return ResponseEntity.ok(offeredSkillDtoList);
    }
}

