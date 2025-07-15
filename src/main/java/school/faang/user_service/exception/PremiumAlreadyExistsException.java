package school.faang.user_service.exception;

public class PremiumAlreadyExistsException extends RuntimeException {
    public PremiumAlreadyExistsException(String message) {
        super(message);
    }
}