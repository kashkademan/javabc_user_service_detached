package school.faang.user_service.avatar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.client.DiceBearClient;
import school.faang.user_service.exception.avatar.AvatarGenerationException;
import school.faang.user_service.service.avatar.AvatarGeneratorService;
import school.faang.user_service.service.s3.S3Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvatarGeneratorServiceTest {

    private static final String AVATAR_FOLDER = "avatars/";
    private static final String EXPECTED_URL = "https://s3.example.com/avatars/generated_avatar.png";
    private static final byte[] AVATAR_BYTES = new byte[]{1, 2, 3, 4};

    @InjectMocks
    private AvatarGeneratorService avatarGeneratorService;

    @Mock
    private S3Service s3Service;

    @Mock
    private DiceBearClient diceBearClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(avatarGeneratorService, "avatarFolder", AVATAR_FOLDER);
        ReflectionTestUtils.setField(avatarGeneratorService, "version", "9.x");
        ReflectionTestUtils.setField(avatarGeneratorService, "style", "pixel-art");
        ReflectionTestUtils.setField(avatarGeneratorService, "format", "png");

    }

    @Test
    void testGenerateAndUploadWhenGenerationSuccessful() {
        when(diceBearClient.getAvatar(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(AVATAR_BYTES);
        when(s3Service.uploadFile(eq(AVATAR_FOLDER), any(MultipartFile.class)))
                .thenReturn(EXPECTED_URL);

        String result = avatarGeneratorService.generateAndUpload();

        assertEquals(EXPECTED_URL, result);
        verify(diceBearClient).getAvatar(anyString(), anyString(), anyString(), anyString());
        verify(s3Service).uploadFile(eq(AVATAR_FOLDER), any(MultipartFile.class));
    }

    @Test
    void testGenerateAndUploadWhenGenerationFailed() {
        when(diceBearClient.getAvatar(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new AvatarGenerationException("Ошибка генерации"));

        assertThrows(AvatarGenerationException.class, () -> avatarGeneratorService.generateAndUpload());

        verify(s3Service, never()).uploadFile(anyString(), any());
    }
}