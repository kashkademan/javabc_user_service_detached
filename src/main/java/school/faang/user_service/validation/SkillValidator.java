package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import static school.faang.user_service.util.LogsConstants.SKILL_ALREADY_EXIST;
import static school.faang.user_service.util.LogsConstants.USER_HAS_SKILL;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkillValidator {
    private final SkillRepository skillRepository;

    public void validateTitleUnique(String title) {
        if (skillRepository.existsByTitle(title)) {
            log.error("Skill '{}' is already existed", title);
            throw new DataValidationException(String.format(SKILL_ALREADY_EXIST, title));
        }
    }

    public void validateUserHasSkill(long userId, long skillId) {
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            log.error("User {} already has skill {}", userId, skillId);
            throw new DataValidationException(String.format(USER_HAS_SKILL, userId, skillId));
        }
    }
}
