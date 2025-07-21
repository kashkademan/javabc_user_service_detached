package school.faang.user_service.exception;

public class AccessDeniedException extends ForbiddenException {

    public AccessDeniedException(String message) {
        super(message);
    }
}
