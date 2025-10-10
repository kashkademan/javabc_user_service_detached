package school.faang.user_service.service.event;


import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
        long currentUserId = OWNER_ID;

        assertThatThrownBy(() -> EventValidator.validateOwner(event, currentUserId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("Only owner can modify event");
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
    void validateOwnerSkills_shouldThrowDataValidationException_ifOwnerHasNoSkills() {
        User owner = new User();
        owner.setSkills(List.of());
        Set<Long> requiredSkills = Set.of(OWNER_ID);

        assertThatThrownBy(() -> EventValidator.validateOwnerSkills(owner, requiredSkills))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("Owner does not have required skills: [1]");
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

        assertThatThrownBy(() -> EventValidator.validateEventDates(start, end))
                .isInstanceOf(DataValidationException.class)
                .hasMessage("Start and end dates cannot be null");
    }

    @Test
    void validateEventDates_shouldThrowIfStartAfterEnd() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 14, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 1, 12, 0);

        assertThatThrownBy(() -> EventValidator.validateEventDates(start, end))
                .isInstanceOf(DataValidationException.class)
                .hasMessage("Start date must be before end date");
    }

    @Test
    void validateEventCreation_shouldValidateSkillsAndDates() {
        Skill skill1 = new Skill();
        skill1.setId(OWNER_ID);
        User owner = new User();
        owner.setSkills(List.of(skill1));
        CreateEventDto dto = new CreateEventDto(
                "Title",
                "Description",
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 1, 12, 0),
                null,
                Set.of(OWNER_ID)
        );

        assertThatCode(() -> EventValidator.validateEventCreation(dto, owner))
                .doesNotThrowAnyException();
    }
}