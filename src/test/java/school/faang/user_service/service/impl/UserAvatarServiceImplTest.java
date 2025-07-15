package school.faang.user_service.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.properties.ProfilePicProperties;
import school.faang.user_service.config.properties.S3Properties;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.AvatarNotFoundException;
import school.faang.user_service.exception.FileTooLargeException;
import school.faang.user_service.exception.InvalidFileTypeException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.userAvatar.UserAvatarValidator;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class UserAvatarServiceImplTest {

    @InjectMocks
    private UserAvatarServiceImpl userAvatarService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private S3Properties s3Properties;

    @Mock
    private ProfilePicProperties profilePicProperties;

    @Mock
    private UserAvatarValidator userAvatarValidator;

    private static final int LARGE_PHOTO_SIZE = 1080;
    private static final int SMALL_PHOTO_SIZE = 170;
    private final Long userId = 1L;

    @Test
    void test_uploadAvatarSuccess() throws IOException {
        User testUser = new User();
        testUser.setId(userId);

        BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(testImage, "png", baos);
        byte[] imageData = baos.toByteArray();

        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream())
                .thenReturn(new ByteArrayInputStream(imageData))
                .thenReturn(new ByteArrayInputStream(imageData));

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(s3Properties.getBucketName()).thenReturn("test-bucket");
        when(profilePicProperties.getLargePhotoSize()).thenReturn(LARGE_PHOTO_SIZE);
        when(profilePicProperties.getSmallPhotoSize()).thenReturn(SMALL_PHOTO_SIZE);
        when(s3Client.putObject(any(PutObjectRequest.class))).thenReturn(new PutObjectResult());

        userAvatarService.uploadAvatar(userId, file);

        verify(userAvatarValidator).validateFile(file);
        verify(userRepository).findById(userId);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertNotNull(savedUser.getUserProfilePic());
        assertNotNull(savedUser.getUserProfilePic().getFileId());
        assertNotNull(savedUser.getUserProfilePic().getSmallFileId());

        verify(s3Client, times(2)).putObject(any(PutObjectRequest.class));
    }

    @Test
    void test_uploadAvatar_throwException_whenFileIsEmpty() {
        MultipartFile file = mock(MultipartFile.class);

        doThrow(new FileTooLargeException("File is empty"))
                .when(userAvatarValidator)
                .validateFile(any(MultipartFile.class));

        FileTooLargeException ex = assertThrows(FileTooLargeException.class, () -> {
            userAvatarService.uploadAvatar(userId, file);
        });

        assertEquals("File is empty", ex.getMessage());
    }

    @Test
    void test_uploadAvatar_throwException_whenFileIsTooLarge() {
        MultipartFile file = mock(MultipartFile.class);

        doThrow(new FileTooLargeException("File size exceeds the limit"))
                .when(userAvatarValidator)
                .validateFile(file);

        FileTooLargeException ex = assertThrows(FileTooLargeException.class, () -> {
            userAvatarService.uploadAvatar(userId, file);
        });

        assertEquals("File size exceeds the limit", ex.getMessage());
    }

    @Test
    void test_uploadAvatar_throwException_whenFileTypeIsInvalid() {
        MultipartFile file = mock(MultipartFile.class);

        doThrow(new InvalidFileTypeException("Only images are allowed"))
                .when(userAvatarValidator).validateFile(file);

        InvalidFileTypeException ex = assertThrows(InvalidFileTypeException.class, () -> {
            userAvatarService.uploadAvatar(userId, file);
        });

        assertEquals("Only images are allowed", ex.getMessage());
    }

    @Test
    void test_uploadAvatar_throwException_whenUserNotFound() {
        MultipartFile file = mock(MultipartFile.class);

        doNothing().when(userAvatarValidator).validateFile(file);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> {
            userAvatarService.uploadAvatar(userId, file);
        });

        assertEquals("User not found with ID " + userId, e.getMessage());
    }

    @Test
    void test_downloadLargeAvatarSuccess() throws IOException {
        User user = new User();
        UserProfilePic userProfilePic = new UserProfilePic("large-file-id", "small-file-id");
        user.setUserProfilePic(userProfilePic);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3Properties.getBucketName()).thenReturn("test-bucket");

        S3Object s3Object = new S3Object();
        byte[] imageBytes = new byte[]{1, 2, 3};
        s3Object.setObjectContent(new S3ObjectInputStream(new ByteArrayInputStream(imageBytes), null));
        when(s3Client.getObject("test-bucket", "large-file-id")).thenReturn(s3Object);

        InputStreamResource result = userAvatarService.downloadLargeAvatar(userId);

        verify(userRepository).findById(userId);
        verify(s3Client).getObject("test-bucket", "large-file-id");

        assertNotNull(result);
        assertTrue(result.getInputStream().available() > 0,
                "Returned InputStream should not be empty");
    }

    @Test
    void test_downloadLargeAvatar_throwException_whenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userAvatarService.downloadLargeAvatar(userId)
        );

        assertEquals("User not found with ID " + userId, exception.getMessage());
    }

    @Test
    void test_downloadLargeAvatar_throwException_whenAvatarNotFound() {
        User userWithoutAvatar = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithoutAvatar));

        AvatarNotFoundException ex = assertThrows(
                AvatarNotFoundException.class,
                () -> userAvatarService.downloadLargeAvatar(userId)
        );

        assertEquals("Avatar not found for user with ID " + userId, ex.getMessage());
    }

    @Test
    void test_downloadSmallAvatarSuccess() throws IOException {
        User user = new User();
        UserProfilePic userProfilePic = new UserProfilePic("large-file-id", "small-file-id");
        user.setUserProfilePic(userProfilePic);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3Properties.getBucketName()).thenReturn("test-bucket");

        S3Object s3Object = new S3Object();
        byte[] imageBytes = new byte[]{1, 2, 3};
        s3Object.setObjectContent(new S3ObjectInputStream(new ByteArrayInputStream(imageBytes), null));
        when(s3Client.getObject("test-bucket", "small-file-id")).thenReturn(s3Object);

        InputStreamResource result = userAvatarService.downloadSmallAvatar(userId);

        verify(userRepository).findById(userId);
        verify(s3Client).getObject("test-bucket", "small-file-id");

        assertNotNull(result);
        assertTrue(result.getInputStream().available() > 0, "Returned InputStream should not be empty");
    }

    @Test
    void test_downloadSmallAvatar_throwException_whenUserNotFound() {
        User userWithoutAvatar = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithoutAvatar));

        AvatarNotFoundException ex = assertThrows(
                AvatarNotFoundException.class,
                () -> userAvatarService.downloadSmallAvatar(userId)
        );

        assertEquals("Avatar not found for user with ID " + userId, ex.getMessage());
    }

    @Test
    void test_downloadSmallAvatar_throwException_whenAvatarNotFound() {
        User userWithoutAvatar = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithoutAvatar));

        AvatarNotFoundException ex = assertThrows(
                AvatarNotFoundException.class,
                () -> userAvatarService.downloadSmallAvatar(userId)
        );

        assertEquals("Avatar not found for user with ID " + userId, ex.getMessage());
    }

    @Test
    void test_deleteAvatarSuccess() {
        User user = new User();
        UserProfilePic userProfilePic = new UserProfilePic("large-file-id", "small-file-id");
        user.setUserProfilePic(userProfilePic);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3Properties.getBucketName()).thenReturn("test-bucket");
        when(s3Client.doesObjectExist("test-bucket", "large-file-id")).thenReturn(true);
        when(s3Client.doesObjectExist("test-bucket", "small-file-id")).thenReturn(true);

        userAvatarService.deleteAvatar(userId);

        verify(s3Client).deleteObject("test-bucket", "large-file-id");
        verify(s3Client).deleteObject("test-bucket", "small-file-id");
        verify(userRepository).save(user);
        assertNull(user.getUserProfilePic());
    }

    @Test
    void test_deleteAvatar_throwsException_whenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> {
            userAvatarService.deleteAvatar(userId);
        });

        assertEquals("User not found with ID " + userId, ex.getMessage());
    }

    @Test
    void test_deleteAvatar_doesNothing_whenUserHasNoAvatar() {
        User user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userAvatarService.deleteAvatar(userId);

        verify(s3Client, never()).deleteObject(anyString(), anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void test_deleteAvatar_oneFileDoesNotExistInS3() {
        User user = new User();
        UserProfilePic userProfilePic = new UserProfilePic("large-file-id", "small-file-id");
        user.setUserProfilePic(userProfilePic);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(s3Properties.getBucketName()).thenReturn("test-bucket");

        when(s3Client.doesObjectExist("test-bucket", "large-file-id")).thenReturn(true);
        when(s3Client.doesObjectExist("test-bucket", "small-file-id")).thenReturn(false);

        userAvatarService.deleteAvatar(userId);

        verify(s3Client).deleteObject("test-bucket", "large-file-id");
        verify(s3Client, never()).deleteObject("test-bucket", "small-file-id");
        verify(userRepository).save(user);
        assertNull(user.getUserProfilePic());
    }
}
