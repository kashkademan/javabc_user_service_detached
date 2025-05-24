package school.faang.user_service.validation.mentorship;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MentorshipValidationTest {
    private static final long MENTOR_ID = 1L;
    private static final long MENTEE_ID = 2L;

    @Test
    void testValidateListUsersWhenListNull() {
        assertThrows(
                DataValidationException.class,
                () -> MentorshipValidation.validateListUsersNotNull(null));
    }

    @Test
    void testValidateListUsersWhenListNotNull() {
        assertDoesNotThrow(
                () -> MentorshipValidation.validateListUsersNotNull(Collections.emptyList()));
    }

    @Test
    void testValidateMentorshipUsersWhenMentorAndMenteeNull() {
        assertThrows(
                DataValidationException.class,
                () -> MentorshipValidation.validateMentorshipUsers(null, null));
    }

    @Test
    void testValidateMentorshipUsersWhenMentorNull() {
        assertThrows(
                DataValidationException.class,
                () -> MentorshipValidation.validateMentorshipUsers(null, new User()));

    }

    @Test
    void testValidateMentorshipUsersWhenMenteeNull() {
        assertThrows(
                DataValidationException.class,
                () -> MentorshipValidation.validateMentorshipUsers(new User(), null));

    }

    @Test
    void testValidateMentorshipUsersWhenMentorAndMenteeNotNull() {
        User mentor = new User();
        User mentee = new User();
        mentor.setId(MENTOR_ID);
        mentee.setId(MENTEE_ID);

        assertDoesNotThrow(
                () -> MentorshipValidation.validateMentorshipUsers(mentor, mentee));
    }

    @Test
    void testValidateMentorshipUsersWhenMentorEqualsMentee() {
        User mentor = new User();
        User mentee = new User();
        mentor.setId(MENTOR_ID);
        mentee.setId(MENTOR_ID);

        assertThrows(
                DataValidationException.class,
                () -> MentorshipValidation.validateMentorshipUsers(mentor, mentee));
    }

    @Test
    void testValidateIsMentorOfWhenMentorHasMentee() {
        User mentor = new User();
        User mentee = new User();
        mentee.setMentors(List.of(mentor));

        assertDoesNotThrow(
                () -> MentorshipValidation.validateIsMentorOf(mentor, mentee)
        );
    }

    @Test
    void testValidateIsMentorOfWhenMentorNotHasMentee() {
        User mentor = new User();
        User mentee = new User();
        mentee.setMentors(Collections.emptyList());

        assertThrows(
                EntityNotFoundException.class,
                () -> MentorshipValidation.validateIsMentorOf(mentor, mentee));
    }

    @Test
    void testValidateIsMenteeOfWhenMenteeHasMentor() {
        User mentor = new User();
        User mentee = new User();
        mentor.setMentees(List.of(mentee));

        assertDoesNotThrow(
                () -> MentorshipValidation.validateIsMenteeOf(mentor, mentee)
        );
    }

    @Test
    void testValidateIsMenteeOfWhenMenteeNotHasMentor() {
        User mentor = new User();
        User mentee = new User();
        mentor.setMentees(Collections.emptyList());

        assertThrows(
                EntityNotFoundException.class,
                () -> MentorshipValidation.validateIsMenteeOf(mentor, mentee));
    }
}