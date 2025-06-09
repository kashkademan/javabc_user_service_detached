package school.faang.user_service.exception.event;

import jakarta.persistence.EntityNotFoundException;

public class EventNotFoundException extends EntityNotFoundException {
    public EventNotFoundException(long goalId) {
        super(String.format("Event with id %d not found", goalId));
    }
}
