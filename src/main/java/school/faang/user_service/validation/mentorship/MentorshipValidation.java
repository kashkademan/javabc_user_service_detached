package school.faang.user_service.validation.mentorship;

import jakarta.persistence.EntityNotFoundException;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.common.DataValidationException;

import java.util.List;
import java.util.Objects;

public class MentorshipValidation {

    public static void validateListUsersNotNull(List<User> users) {
        if (users == null) {
            throw new DataValidationException("The user list cannot be null");
        }
    }

    public static void validateMentorshipUsers(User mentor, User mentee) {
        if (mentor == null || mentee == null) {
            throw new DataValidationException("The mentor and the student cannot be null");
        }

        if (Objects.equals(mentor, mentee)) {
            throw new DataValidationException(
                    String.format("A user with ID=%d cannot be their own mentor", mentor.getId())
            );
        }
    }

    public static void validateIsMentorOf(User mentor, User mentee) {
        if (!mentee.getMentors().contains(mentor)) {
            throw new EntityNotFoundException(
                    String.format("User with ID=%d is not a mentor for student with ID=%d",
                            mentor.getId(), mentee.getId())
            );
        }
    }

    public static void validateIsMenteeOf(User mentor, User mentee) {
        if (!mentor.getMentees().contains(mentee)) {
            throw new EntityNotFoundException(
                    String.format("User with ID=%d is not a mentor for student with ID=%d",
                            mentor.getId(), mentee.getId())
            );
        }
    }
}
