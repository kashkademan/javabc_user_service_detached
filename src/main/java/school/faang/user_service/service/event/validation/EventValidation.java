package school.faang.user_service.service.event.validation;

import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class EventValidation {

    public static void checkEventOwner(Long eventOwnerId, Long userId) {
        if (!Objects.equals(userId, eventOwnerId)) {
            throw new ForbiddenException(String.format("User %d doesn't match event owner!", userId));
        }
    }

    public static void checkEventDates(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        StringBuilder stringBuilder = new StringBuilder();
        boolean exceptionMustBeThrown = false;
        if (startDate.isBefore(now)) {
            stringBuilder.append("The start date must be no earlier than the current date.");
            exceptionMustBeThrown = true;
        }
        if (endDate.isBefore(startDate)) {
            stringBuilder.append("The start date must be earlier than the end date.");
            exceptionMustBeThrown = true;
        }
        if (exceptionMustBeThrown) {
            throw new DataValidationException(stringBuilder.toString());
        }
    }

    public static void checkOwnerSkills(List<Skill> requiredSkills, List<Skill> userSkills) {
        if (!userSkills.containsAll(requiredSkills)) {
            throw new DataValidationException("The owner does not have the necessary skills");
        }
    }
}
