package school.faang.user_service.exception.user;

import java.util.NoSuchElementException;

public class UserNotFoundException extends NoSuchElementException {
    public UserNotFoundException(long userId) {
        super(String.format("User with id %d not found", userId));
    }
}
