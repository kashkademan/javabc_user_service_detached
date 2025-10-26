package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserAvatarUploadDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.ImageProcessing;
import school.faang.user_service.service.S3AvatarService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private ImageProcessing imageProcessing;
    @Mock
    private S3AvatarService s3AvatarService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(userService, "avatarMaxSizeMb", 5);
    }

    @Test
    void uploadAvatar_whenFileIsEmpty_shouldThrowDataValidationException() {
        when(userContext.getUserId()).thenReturn(1L);
        when(userRepository.getByIdOrThrow(1L)).thenReturn(new User());

        MockMultipartFile file = new MockMultipartFile("file", new byte[0]);
        UserAvatarUploadDto dto = new UserAvatarUploadDto(file);

        assertThrows(DataValidationException.class, () -> userService.uploadAvatar(dto));
    }

    @Test
    void uploadAvatar_whenFileIsInvalidImage_shouldThrowDataValidationException() {
        when(userContext.getUserId()).thenReturn(1L);
        when(userRepository.getByIdOrThrow(1L)).thenReturn(new User());

        byte[] bytes = "not-an-image".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.png",
                "image/png",
                bytes
        );
        UserAvatarUploadDto dto = new UserAvatarUploadDto(file);

        assertThrows(DataValidationException.class, () -> userService.uploadAvatar(dto));
    }

    @Test
    void uploadAvatar_whenNewAvatarProvided_shouldCreateProfilePic() throws IOException {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);

        when(userContext.getUserId()).thenReturn(1L);
        User user = new User();
        when(userRepository.getByIdOrThrow(1L)).thenReturn(user);
        when(imageProcessing.resizeImage(any(), anyInt())).thenReturn(image);
        when(s3AvatarService.uploadImage(any(), anyString(), anyString())).thenReturn("s3-key");

        byte[] imageBytes = outputStream.toByteArray();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.png",
                "image/png",
                imageBytes
        );
        UserAvatarUploadDto dto = new UserAvatarUploadDto(file);

        userService.uploadAvatar(dto);

        assertNotNull(user.getUserProfilePic());
        assertEquals("s3-key", user.getUserProfilePic().getFileId());
        assertEquals("s3-key", user.getUserProfilePic().getSmallFileId());
        verify(userRepository).save(user);
    }

    @Test
    void uploadAvatar_whenExistingAvatarProvided_shouldDeleteOldAvatar() throws IOException {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);

        when(userContext.getUserId()).thenReturn(1L);

        UserProfilePic oldPic = new UserProfilePic();
        oldPic.setFileId("oldBig");
        oldPic.setSmallFileId("oldSmall");
        User user = new User();
        user.setUserProfilePic(oldPic);

        when(userRepository.getByIdOrThrow(1L)).thenReturn(user);
        when(imageProcessing.resizeImage(any(), anyInt())).thenReturn(image);
        when(s3AvatarService.uploadImage(any(), anyString(), anyString())).thenReturn("newKey");

        byte[] imageBytes = outputStream.toByteArray();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "file.png",
                "image/png",
                imageBytes
        );
        UserAvatarUploadDto dto = new UserAvatarUploadDto(file);

        userService.uploadAvatar(dto);

        verify(s3AvatarService).deleteImage("oldBig");
        verify(s3AvatarService).deleteImage("oldSmall");
        assertEquals("newKey", user.getUserProfilePic().getFileId());
        assertEquals("newKey", user.getUserProfilePic().getSmallFileId());
    }

    @Test
    void deleteAvatar_whenNoAvatar_shouldDoNothing() {
        when(userContext.getUserId()).thenReturn(1L);
        User user = new User();
        when(userRepository.getByIdOrThrow(1L)).thenReturn(user);

        assertDoesNotThrow(() -> userService.deleteAvatar());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteAvatar_whenAvatarExists_shouldDeleteFilesAndClearProfilePic() {
        when(userContext.getUserId()).thenReturn(1L);
        UserProfilePic pic = new UserProfilePic();
        pic.setFileId("big");
        pic.setSmallFileId("small");
        User user = new User();
        user.setUserProfilePic(pic);
        when(userRepository.getByIdOrThrow(1L)).thenReturn(user);

        userService.deleteAvatar();

        verify(s3AvatarService).deleteImage("big");
        verify(s3AvatarService).deleteImage("small");
        assertNull(user.getUserProfilePic());
        verify(userRepository).save(user);
    }

    @Test
    void getAvatar_whenNoAvatar_shouldThrowDataValidationException() {
        User user = new User();
        when(userRepository.getByIdOrThrow(1L)).thenReturn(user);

        assertThrows(DataValidationException.class, () -> userService.getAvatar(1L, "big"));
    }

    @Test
    void getAvatar_whenInvalidSize_shouldThrowDataValidationException() {
        UserProfilePic pic = new UserProfilePic();
        pic.setFileId("big");
        pic.setSmallFileId("small");
        User user = new User();
        user.setUserProfilePic(pic);
        when(userRepository.getByIdOrThrow(1L)).thenReturn(user);

        assertThrows(DataValidationException.class, () -> userService.getAvatar(1L, "invalid"));
    }

    @Test
    void getAvatar_whenBigRequested_shouldReturnBigFile() {
        UserProfilePic pic = new UserProfilePic();
        pic.setFileId("bigKey");
        pic.setSmallFileId("smallKey");
        User user = new User();
        user.setUserProfilePic(pic);
        when(userRepository.getByIdOrThrow(1L)).thenReturn(user);
        when(s3AvatarService.downloadImage("bigKey")).thenReturn(new byte[]{1, 2, 3});
        when(s3AvatarService.getContentType("bigKey")).thenReturn("image/png");

        ResponseEntity<byte[]> response = userService.getAvatar(1L, "big");

        assertArrayEquals(new byte[]{1, 2, 3}, response.getBody());
        assertEquals("image/png", response.getHeaders().getFirst("Content-Type"));
    }

    @Test
    void getAvatar_whenSmallRequested_shouldReturnSmallFile() {
        UserProfilePic pic = new UserProfilePic();
        pic.setFileId("bigKey");
        pic.setSmallFileId("smallKey");
        User user = new User();
        user.setUserProfilePic(pic);
        when(userRepository.getByIdOrThrow(1L)).thenReturn(user);
        when(s3AvatarService.downloadImage("smallKey")).thenReturn(new byte[]{4, 5, 6});
        when(s3AvatarService.getContentType("smallKey")).thenReturn("image/webp");

        ResponseEntity<byte[]> response = userService.getAvatar(1L, "small");

        assertArrayEquals(new byte[]{4, 5, 6}, response.getBody());
        assertEquals("image/webp", response.getHeaders().getFirst("Content-Type"));
    }
}
