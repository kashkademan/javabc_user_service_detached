package school.faang.user_service.exception.user;

public class PhoneAlreadyExistsException extends RuntimeException {
    public PhoneAlreadyExistsException(String msg) {
        super(msg);
    }
}
