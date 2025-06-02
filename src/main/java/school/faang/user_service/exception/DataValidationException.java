package school.faang.user_service.exception;

public class DataValidationException extends RuntimeException {
    public DataValidationException() {
        super("Data validation failed");
    }

    public DataValidationException(String message) {
        super(message);
    }
}
