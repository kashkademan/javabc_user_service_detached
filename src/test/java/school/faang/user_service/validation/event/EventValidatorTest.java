package school.faang.user_service.validation.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.skill.Skill;
import school.faang.user_service.exception.event.EventValidationException;
import school.faang.user_service.service.skill.SkillService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventValidatorTest {
    @Mock
    private SkillService skillService;
    @InjectMocks
    private EventValidator eventValidator;
    private static final long USER_ID = 10L;
    private Skill skill1;
    private Skill skill2;
    private Skill skill3;
    private List<Skill> requiredSkills;


    @BeforeEach
    void setUp() {
        skill1 = new Skill();
        skill1.setId(1L);
        skill2 = new Skill();
        skill2.setId(2L);
        skill3 = new Skill();
        skill3.setId(3L);

        requiredSkills = List.of(skill1, skill2);
    }

    @Test
    void testValidateEventDates_validDates() {
        LocalDateTime start = LocalDateTime.of(2025, 6, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 6, 2, 10, 0);

        assertDoesNotThrow(() -> eventValidator.validateEventDates(start, end));
    }

    @Test
    void testValidateEventDates_endBeforeStart() {
        LocalDateTime start = LocalDateTime.of(2025, 6, 2, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 6, 1, 10, 0);

        assertThrows(EventValidationException.class, () -> eventValidator.validateEventDates(start, end));
    }

    @Test
    void testValidateOwnerHasSkills_userHasAllSkills() {
        List<Skill> userSkills = List.of(skill1, skill2, skill3);

        when(skillService.getSkillsByUserId(USER_ID)).thenReturn(userSkills);

        assertDoesNotThrow(() -> eventValidator.validateOwnerHasSkills(USER_ID, requiredSkills));
    }

    @Test
    void validateOwnerHasSkills_userMissingSkills_throwsException() {
        Skill requiredSkill = new Skill();
        requiredSkill.setId(5L);

        when(skillService.getSkillsByUserId(USER_ID)).thenReturn(requiredSkills);

        assertThrows(EventValidationException.class, () -> eventValidator.validateOwnerHasSkills(USER_ID, List.of(requiredSkill)));
    }
}
