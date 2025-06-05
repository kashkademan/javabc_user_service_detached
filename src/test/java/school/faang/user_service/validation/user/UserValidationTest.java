package school.faang.user_service.validation.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.common.RecordNotFoundException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.SettingsConstants.MAX_FILE_SIZE;

@ExtendWith(MockitoExtension.class)
public class UserValidationTest {

    private UserValidation userValidation;

    @BeforeEach
    void setUp() {
        userValidation = new UserValidation();
    }

    @Test
    public void testValidateMaxFileSizeWhenSuccess() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(MAX_FILE_SIZE - 1);

        assertDoesNotThrow(() -> userValidation.validateMaxFileSize(file));
    }

    @Test
    public void testValidateMaxFileSizeWhenMaxFileSizeExceeded() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(MAX_FILE_SIZE + 1);

        assertThrows(MaxUploadSizeExceededException.class, () -> userValidation.validateMaxFileSize(file));
    }

    @Test
    public void testValidateProfilePicNotNullWhenSuccess() {
        long userId = 1L;
        UserProfilePic profilePic = mock(UserProfilePic.class);

        assertDoesNotThrow(() -> userValidation.validateProfilePicNotNull(profilePic, userId));
    }

    @Test
    public void testValidateProfilePicNotNullWhenProfilePicIsNull() {
        long userId = 1L;

        assertThrows(RecordNotFoundException.class, () -> userValidation.validateProfilePicNotNull(null, userId));
    }

}
