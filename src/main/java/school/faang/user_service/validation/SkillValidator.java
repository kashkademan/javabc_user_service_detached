package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.constant.SkillConstant;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import static school.faang.user_service.constant.SkillConstant.BLANK_SKILL_TITLE;
import static school.faang.user_service.constant.SkillConstant.SKILL_ALREADY_EXIST;

@Component
@RequiredArgsConstructor
@Slf4j
public class SkillValidator {
    private final SkillRepository skillRepository;

    public void validateTitleUnique(String title) {
        if (skillRepository.existsByTitle(title)) {
            log.error(SKILL_ALREADY_EXIST, title);
            throw new DataValidationException(SKILL_ALREADY_EXIST, title);
        }
    }

    public void validateTitleBlank(String title) {
        if (title == null || title.isBlank()) {
            log.error(BLANK_SKILL_TITLE);
            throw new DataValidationException(BLANK_SKILL_TITLE);
        }
    }

}
