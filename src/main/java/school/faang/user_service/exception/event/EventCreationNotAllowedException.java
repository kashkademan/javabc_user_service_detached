package school.faang.user_service.exception.event;

public class EventCreationNotAllowedException extends RuntimeException {
    public EventCreationNotAllowedException(String message) {
        super(message);
    }
}
