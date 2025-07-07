package school.faang.user_service.controller.skill;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.skill.SkillServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SkillController {

    SkillServiceImpl skillService;
    UserContext userContext;

    @PostMapping("/skill")
    public SkillDto create(CreateSkillDto skillDto) {
        validateString(skillDto.title(), "title");
        validateNotNull(skillDto.title(), "title");
        return skillService.create(skillDto);
    }

    @GetMapping("/skill")
    public List<SkillDto> getByUserId(Long userId) {
        validateNotNull(userId, "userId");
        return skillService.getByUserId(userId);
    }

    @GetMapping("/skill/offers")
    public List<SkillCandidateDto> getOfferedSkills() {
        return skillService.getOfferedSkills(userContext.getUserId());
    }

    private void validateString(String value, String paramName) {
        if (StringUtils.isNotBlank(value)) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }

    private void validateNotNull(Object value, String paramName) {
        if (value == null) {
            throw new DataValidationException(paramName + " should be present!");
        }
    }

}
