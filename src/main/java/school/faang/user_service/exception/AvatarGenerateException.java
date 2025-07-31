package school.faang.user_service.exception;

import java.io.IOException;

public class AvatarGenerateException extends RuntimeException {
    public AvatarGenerateException(String message) {
        super(message);
    }

    public AvatarGenerateException(String message, IOException e) {
        super(message, e);
    }
}
