package school.faang.user_service.validation.mentorship;

import jakarta.persistence.EntityNotFoundException;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;
import java.util.Objects;

public class MentorshipValidation {

    public static void validateMenteesNonEmpty(List<User> mentees) {
        if (mentees == null || mentees.isEmpty()) {
            throw new DataValidationException("У пользователя нету учеников");
        }
    }

    public static void validateMentorsNonEmpty(List<User> mentors) {
        if (mentors == null || mentors.isEmpty()) {
            throw new DataValidationException("У пользователя нету наставников");
        }
    }

    public static void validateMentorshipUsers(User mentor, User mentee) {
        if (mentor == null || mentee == null) {
            throw new DataValidationException("Ментор и ученик не могут быть null");
        }

        if (Objects.equals(mentor, mentee)) {
            throw new DataValidationException(
                    String.format("Пользователь с ID=%d не может быть ментором самому себе", mentor.getId())
            );
        }
    }

    public static void validateIsMentorOf(User mentor, User mentee) {
        if (!mentee.getMentors().contains(mentor)) {
            throw new EntityNotFoundException(
                    String.format("Пользователь с ID=%d не является ментором для ученика с ID=%d",
                            mentor.getId(), mentee.getId())
            );
        }
    }

    public static void validateIsMenteeOf(User mentor, User mentee) {
        if (!mentor.getMentees().contains(mentee)) {
            throw new EntityNotFoundException(
                    String.format("Пользователь с ID=%d не является ментором для ученика с ID=%d",
                            mentor.getId(), mentee.getId())
            );
        }
    }
}
