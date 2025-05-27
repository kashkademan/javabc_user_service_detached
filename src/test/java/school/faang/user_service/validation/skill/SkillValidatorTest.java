package school.faang.user_service.validation.skill;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.common.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.LogsConstants.SKILL_ALREADY_EXIST;
import static school.faang.user_service.util.LogsConstants.USER_HAS_SKILL;

@ExtendWith(MockitoExtension.class)
public class SkillValidatorTest {
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private SkillValidator validator;

    private static final String title = "Java";
    private static final long skillId  = 1L;
    private static final long userId = 2L;

    @Test
    public void validateTitleUniqueShouldBeSuccessful() {
        when(skillRepository.existsByTitle(title)).thenReturn(false);

        assertDoesNotThrow(() -> validator.validateTitleUnique(title));
    }

    @Test
    public void validateTitleUniqueWhenTitleExists() {
        when(skillRepository.existsByTitle(title)).thenReturn(true);

        DataValidationException dataValidationException =
                assertThrows(DataValidationException.class, () -> validator.validateTitleUnique(title));
        assertEquals(String.format(SKILL_ALREADY_EXIST, title), dataValidationException.getMessage());
    }

    @Test
    public void validateUserHasSkillShouldBeSuccessful() {
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> validator.validateUserHasSkill(userId, skillId));
    }

    @Test
    public void validateUserHasSkillWhenHasSkill() {
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(new Skill()));

        DataValidationException dataValidationException =
                assertThrows(DataValidationException.class, () -> validator.validateUserHasSkill(userId, skillId));
        assertEquals(String.format(USER_HAS_SKILL, userId, skillId), dataValidationException.getMessage());
    }
}
