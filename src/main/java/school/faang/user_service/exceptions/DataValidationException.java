package school.faang.user_service.exceptions;

public class DataValidationException extends RuntimeException {
    public DataValidationException(String errorMessage) {
        super(errorMessage);
    }
}
