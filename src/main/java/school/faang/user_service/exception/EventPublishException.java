package school.faang.user_service.exception;

public class EventPublishException extends RuntimeException {
    public EventPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
