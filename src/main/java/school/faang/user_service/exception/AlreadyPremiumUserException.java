package school.faang.user_service.exception;

public class AlreadyPremiumUserException extends RuntimeException {
    public AlreadyPremiumUserException(String message) {
        super(message);
    }
}