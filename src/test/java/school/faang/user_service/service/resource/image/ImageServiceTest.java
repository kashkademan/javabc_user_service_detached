package school.faang.user_service.service.resource.image;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import school.faang.user_service.client.dice_bear.DiceBearClient;
import school.faang.user_service.entity.resource.Resource;
import school.faang.user_service.service.resource.ResourceService;
import school.faang.user_service.service.s3.S3KeyGenerator;
import school.faang.user_service.service.s3.S3Service;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @Mock
    private DiceBearClient diceBearClient;

    @Mock
    private S3Service s3Service;
    @Spy
    private S3KeyGenerator s3KeyGenerator;
    @Mock
    private ResourceService resourceService;
    @Captor
    private ArgumentCaptor<MediaType> mediaTypeCaptor;
    @Captor
    private ArgumentCaptor<Resource> resourceCaptor;
    @InjectMocks
    private ImageService imageService;

    private static final Long USER_ID = 42L;

    @Test
    void testGenerateRandomUserAvatar_returnCorrectResource() {
        byte[] dummyImage = "<svg>avatar</svg>".getBytes(StandardCharsets.UTF_8);

        when(diceBearClient.getRandomAvatar(mediaTypeCaptor.capture())).thenReturn(dummyImage);
        when(resourceService.createResource(resourceCaptor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Resource result = imageService.generateRandomUserAvatar(USER_ID);

        MediaType type = mediaTypeCaptor.getValue();
        Resource resource = resourceCaptor.getValue();

        assertNotNull(result);
        assertEquals(type.toString(), result.getContentType());
        assertEquals(dummyImage.length, result.getSize());
        verify(s3Service).uploadFile(eq(dummyImage), any(), eq(type));
        verify(resourceService).createResource(resource);
    }

    @Test
    void testGenerateRandomUserAvatar_requestInDiceBearFails() {

        when(diceBearClient.getRandomAvatar(mediaTypeCaptor.capture()))
                .thenThrow(WebClientResponseException.class);

        assertThrows(WebClientResponseException.class, () -> imageService.generateRandomUserAvatar(USER_ID));
        verify(s3Service, never()).uploadFile(any(), any(), any());
    }
}