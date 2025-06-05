package school.faang.user_service.validation.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.common.RecordNotFoundException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class UserValidationTest {

    private UserValidation userValidation;
    private static final long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        userValidation = new UserValidation();
    }

    @Test
    public void testValidateProfilePicNotNullWhenSuccess() {
        UserProfilePic profilePic = mock(UserProfilePic.class);

        assertDoesNotThrow(() -> userValidation.validateProfilePicNotNull(profilePic, USER_ID));
    }

    @Test
    public void testValidateProfilePicNotNullWhenProfilePicIsNull() {
        assertThrows(RecordNotFoundException.class, () -> userValidation.validateProfilePicNotNull(null, USER_ID));
    }
}
