package school.faang.user_service.exception;

public class EventCleanupException extends RuntimeException {
    public EventCleanupException(String message) {
        super(message);
    }

    public EventCleanupException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
