package school.faang.user_service.exception.user;

import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException {
    private final UserField field;
    private final String value;

    public UserAlreadyExistsException(UserField field, String value) {
        super(String.format("%s %s already exists", field, value));
        this.field = field;
        this.value = value;
    }

    public enum UserField {
        EMAIL,
        USERNAME,
        PHONE
    }
}
