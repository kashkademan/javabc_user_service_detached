package school.faang.user_service.exception;

public class TimeLimitException extends RuntimeException {

    public TimeLimitException(String message) {
        super(message);
    }
}
