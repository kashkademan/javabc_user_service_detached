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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.LogsConstants.SKILL_ALREADY_EXIST;
import static school.faang.user_service.util.LogsConstants.USER_HAS_SKILL;

@ExtendWith(MockitoExtension.class)
public class SkillValidatorTest {
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private SkillValidator validator;

    private static final String TITLE = "Java";
    private static final long SKILL_ID = 1L;
    private static final long USER_ID = 2L;

    @Test
    public void testValidateTitleUniqueShouldBeSuccessful() {
        when(skillRepository.existsByTitle(TITLE)).thenReturn(false);

        assertDoesNotThrow(() -> validator.validateTitleUnique(TITLE));
        verify(skillRepository).existsByTitle(TITLE);
    }

    @Test
    public void testValidateTitleUniqueWhenTitleExists() {
        when(skillRepository.existsByTitle(TITLE)).thenReturn(true);

        DataValidationException dataValidationException =
                assertThrows(DataValidationException.class, () -> validator.validateTitleUnique(TITLE));
        assertEquals(String.format(SKILL_ALREADY_EXIST, TITLE), dataValidationException.getMessage());
        verify(skillRepository).existsByTitle(TITLE);
    }

    @Test
    public void testValidateUserHasSkillShouldBeSuccessful() {
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> validator.validateUserHasSkill(USER_ID, SKILL_ID));
        verify(skillRepository).findUserSkill(SKILL_ID, USER_ID);
    }

    @Test
    public void testValidateUserHasSkillWhenHasSkill() {
        when(skillRepository.findUserSkill(SKILL_ID, USER_ID)).thenReturn(Optional.of(new Skill()));

        DataValidationException dataValidationException =
                assertThrows(DataValidationException.class, () -> validator.validateUserHasSkill(USER_ID, SKILL_ID));
        assertEquals(String.format(USER_HAS_SKILL, USER_ID, SKILL_ID), dataValidationException.getMessage());
        verify(skillRepository).findUserSkill(SKILL_ID, USER_ID);
    }
}
