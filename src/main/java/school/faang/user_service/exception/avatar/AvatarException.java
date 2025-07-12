package school.faang.user_service.exception.avatar;

public class AvatarException extends RuntimeException {
    public AvatarException(String message) {
        super(message);
    }

    public AvatarException(String message, Throwable cause) {
        super(message, cause);
    }
}