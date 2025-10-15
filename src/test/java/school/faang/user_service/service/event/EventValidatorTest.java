package school.faang.user_service.service.event;


import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.helpers.TestUtils;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventValidatorTest {
    private static final Long OWNER_ID = 1L;
    private static final Long USER_ID = 2L;

    @Test
    void validateOwner_shouldPass_whenUserIsOwner() {
        Event event = new Event();
        User owner = new User();
        owner.setId(OWNER_ID);
        event.setOwner(owner);

        assertDoesNotThrow(() -> EventValidator.validateOwner(event, OWNER_ID));
    }

    @Test
    void validateOwner_shouldThrowForbiddenException_ifOwnerDoesNotMatch() {
        User owner = new User();
        owner.setId(USER_ID);
        Event event = new Event();
        event.setOwner(owner);

        ForbiddenException ex = assertThrows(
                ForbiddenException.class,
                () -> EventValidator.validateOwner(event, OWNER_ID)
        );
        assertEquals("Only owner can modify event", ex.getMessage());
    }

    @Test
    void validateOwner_shouldThrow_whenOwnerIsNull() {
        Event event = new Event();
        event.setOwner(null);

        assertThrows(IllegalArgumentException.class, () -> EventValidator.validateOwner(event, OWNER_ID));
    }

    @Test
    void validateOwnerSkills_shouldDoNothing_ifSkillsIdIsNull() {
        User owner = new User();

        assertThatCode(() -> EventValidator.validateOwnerSkills(owner, null))
                .doesNotThrowAnyException();
    }

    @Test
    void validateOwnerSkills_shouldPass_whenSkillsAreEmpty() {
        User owner = new User();
        owner.setSkills(List.of());
        Set<Long> requiredSkills = Set.of();

        assertDoesNotThrow(() -> EventValidator.validateOwnerSkills(owner, requiredSkills));
    }

    @Test
    void validateOwnerSkills_shouldDoNothing_ifOwnerHasAllRequiredSkills() {
        Skill skill1 = new Skill();
        skill1.setId(OWNER_ID);
        Skill skill2 = new Skill();
        skill2.setId(USER_ID);
        User owner = new User();
        owner.setSkills(List.of(skill1, skill2));
        Set<Long> requiredSkills = Set.of(OWNER_ID, USER_ID);

        assertThatCode(() -> EventValidator.validateOwnerSkills(owner, requiredSkills))
                .doesNotThrowAnyException();
    }

    @Test
    void validateEventDates_shouldPass_whenStartBeforeEnd() {
        LocalDateTime start = LocalDateTime.of(2025, 10, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2025, 10, 1, 14, 0);

        assertDoesNotThrow(() -> EventValidator.validateEventDates(start, end));
    }

    @Test
    void validateEventDates_shouldThrow_whenDatesNull() {
        assertThrows(DataValidationException.class,
                () -> EventValidator.validateEventDates(null, null));
    }

    @Test
    void validateEventDates_shouldThrowIfStartDateIsNull() {
        LocalDateTime start = null;
        LocalDateTime end = LocalDateTime.of(2025, 1, 1, 12, 0);

        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "Start and end dates cannot be null",
                () -> EventValidator.validateEventDates(start, end)
        );
    }

    @Test
    void validateEventDates_shouldThrowIfStartAfterEnd() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 14, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 1, 12, 0);

        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "Start date must be before end date",
                () -> EventValidator.validateEventDates(start, end)
        );
    }

    @Test
    void validateEventCreation_shouldValidateSkillsAndDates() {
        Skill skill1 = new Skill();
        skill1.setId(OWNER_ID);

        User owner = new User();
        owner.setSkills(List.of(skill1));

        EventCreateDto dto = EventCreateDto.builder()
                .title("Title")
                .description("Description")
                .startDate(LocalDateTime.of(2025, 1, 1, 10, 0))
                .endDate(LocalDateTime.of(2025, 1, 1, 12, 0))
                .type(null)
                .skillsId(Set.of(OWNER_ID))
                .build();

        assertDoesNotThrow(() -> EventValidator.validateEventCreation(dto, owner));
    }
}