package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.s3.FileException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.s3.S3Service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @InjectMocks
    private UserProfileService userProfileService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Service s3Service;

    private static final long USER_ID = 123L;
    private static final String SMALL_FILE_ID = "small/avatar.png";
    private static final String FILE_ID = "full/avatar.png";
    private static final String PRESIGNED_URL = "https://s3.example.com/profile/avatar.png";

    private User validUser;

    @BeforeEach
    void setUp() {
        UserProfilePic profilePic = new UserProfilePic();
        profilePic.setSmallFileId(SMALL_FILE_ID);
        profilePic.setFileId(FILE_ID);

        validUser = new User();
        validUser.setId(USER_ID);
        validUser.setUserProfilePic(profilePic);
    }

    @Test
    void testGeneratePresignedUrlForSmallFile() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(validUser));
        when(s3Service.generatePresignedUrl(SMALL_FILE_ID)).thenReturn(PRESIGNED_URL);

        String actualUrl = userProfileService.generatePresignedUrl(USER_ID, true);

        assertEquals(PRESIGNED_URL, actualUrl);
        verify(userRepository).findById(USER_ID);
        verify(s3Service).generatePresignedUrl(SMALL_FILE_ID);
    }

    @Test
    void testGeneratePresignedUrlForDefaultFile() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(validUser));
        when(s3Service.generatePresignedUrl(FILE_ID)).thenReturn(PRESIGNED_URL);

        String actualUrl = userProfileService.generatePresignedUrl(USER_ID, false);

        assertEquals(PRESIGNED_URL, actualUrl);
        verify(userRepository).findById(USER_ID);
        verify(s3Service).generatePresignedUrl(FILE_ID);
    }

    @Test
    void testGeneratePresignedUrlWhenUserNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userProfileService.generatePresignedUrl(USER_ID, true));

        assertEquals("Пользователь не найден!", exception.getMessage());
        verify(userRepository).findById(USER_ID);
        verifyNoInteractions(s3Service);
    }

    @Test
    void testGeneratePresignedUrlWhenProfilePicNotFound() {
        validUser.setUserProfilePic(null);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(validUser));

        FileException exception = assertThrows(FileException.class, () ->
                userProfileService.generatePresignedUrl(USER_ID, true));

        assertEquals("У данного пользователя нет изображения профиля", exception.getMessage());
        verify(userRepository).findById(USER_ID);
        verifyNoInteractions(s3Service);
    }
}