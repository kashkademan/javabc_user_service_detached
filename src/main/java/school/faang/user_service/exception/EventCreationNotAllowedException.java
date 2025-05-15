package school.faang.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class EventCreationNotAllowedException extends RuntimeException {
    public EventCreationNotAllowedException(String message) {
        super(message);
    }
}
