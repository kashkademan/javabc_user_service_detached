package school.faang.user_service.service.image;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import school.faang.user_service.client.DiceBearClient;
import school.faang.user_service.entity.resource.Resource;
import school.faang.user_service.service.s3.S3Folder;
import school.faang.user_service.service.s3.S3Service;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private DiceBearClient diceBearClient;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private ImageService imageService;

    @Test
    void generateRandomUserAvatar_returnCorrectResource() {
        long userId = 42L;
        byte[] dummyImage = "<svg>avatar</svg>".getBytes(StandardCharsets.UTF_8);
        String expectedFileKey = "avatars/user_42_default_avatar.svg";
        String expectedFileName = "user_42_default_avatar.svg";
        MediaType type = new MediaType("image", "svg+xml");
        String expectedContentType = "image/svg+xml";

        when(diceBearClient.getRandomAvatar()).thenReturn(dummyImage);
        when(s3Service.uploadFile(dummyImage, expectedFileName,
                type, S3Folder.AVATARS))
                .thenReturn(expectedFileKey);

        Resource result = imageService.generateRandomUserAvatar(userId);

        assertNotNull(result);
        assertEquals(expectedFileKey, result.getFileKey());
        assertEquals(expectedFileName, result.getFileName());
        assertEquals(type.toString(), result.getContentType());
        assertEquals(dummyImage.length, result.getSize());
    }
}