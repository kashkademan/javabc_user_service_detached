package school.faang.user_service.validation;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventValidator {
    private final UserContext userContext;
    private final UserRepository userRepository;

    private static final String ERROR_MESSAGE_FOR_NOT_OWNER = "You are not the owner of this event";

    public void validateEventOwnership(Event event) {
        Long currentUserId = userContext.getUserId();
        if (!event.getOwner().getId().equals(currentUserId)) {
            throw new ForbiddenException(ERROR_MESSAGE_FOR_NOT_OWNER);
        }
    }

    public User validateAndGetUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
    }

    public void validateEventNotInPast(LocalDateTime startDate) {
        if (!startDate.isAfter(LocalDateTime.now())) {
            throw new ValidationException("Event cannot start in the past");
        }
    }

    public void validateEventDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (!endDate.isAfter(startDate)) {
            throw new ValidationException("Event end date must be after start date");
        }
    }
}
