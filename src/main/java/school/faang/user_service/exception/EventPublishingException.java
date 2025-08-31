package school.faang.user_service.exception;


public class EventPublishingException extends RuntimeException {
    public EventPublishingException(String message, Exception e) {
        super(message);
    }
}
