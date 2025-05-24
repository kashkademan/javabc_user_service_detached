package school.faang.user_service.exception.authorization;

public class UserUnauthorizedException extends RuntimeException {
    public UserUnauthorizedException(String msg) {
        super(msg);
    }
}
