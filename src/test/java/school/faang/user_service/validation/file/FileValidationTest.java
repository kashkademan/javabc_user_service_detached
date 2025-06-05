package school.faang.user_service.validation.file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.SettingsConstants.MAX_FILE_SIZE;

@ExtendWith(MockitoExtension.class)
public class FileValidationTest {
    private FileValidation fileValidation;

    @BeforeEach
    void setUp() {
        fileValidation = new FileValidation();
    }
    @Test
    public void testValidateMaxFileSizeWhenSuccess() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(MAX_FILE_SIZE - 1);

        assertDoesNotThrow(() -> fileValidation.validateMaxFileSize(file));
    }

    @Test
    public void testValidateMaxFileSizeWhenMaxFileSizeExceeded() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(MAX_FILE_SIZE + 1);

        assertThrows(MaxUploadSizeExceededException.class, () -> fileValidation.validateMaxFileSize(file));
    }
}
