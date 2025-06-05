package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.resource.S3FileDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.users.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.image.ImageResizer;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validation.user.UserValidation;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.SettingsConstants.AVATAR_FOLDER;
import static school.faang.user_service.util.SettingsConstants.AVATAR_MINI_FOLDER;
import static school.faang.user_service.util.SettingsConstants.MAX_SIDE_SIZE;
import static school.faang.user_service.util.SettingsConstants.MAX_SIDE_SIZE_MINI;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final long USER_ID = 1L;
    private static final long USER_ID_TWO = 2L;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserValidation userValidation;
    @Mock
    private S3Service s3Service;
    @Mock
    private UserContext userContext;
    @Mock
    private ImageResizer imageResizer;
    @Mock
    private MultipartFile file;
    @Mock
    private MultipartFile resizedFile;
    @Mock
    private MultipartFile resizedMiniFile;

    @InjectMocks
    private UserService userService;

    private User userOne;
    private User userTwo;

    private final static String FILE_NAME = "test.jpg";
    private final static String FILE_ID = String.format("%s/%s", AVATAR_FOLDER, FILE_NAME);
    private final static String MINI_FILE_ID = String.format("%s/%s", AVATAR_MINI_FOLDER, FILE_NAME);
    private final static String CONTENT_TYPE = "image/jpeg";

    @BeforeEach
    void setUp() {
        userOne = new User();
        userTwo = new User();
        userOne.setId(USER_ID);
        userTwo.setId(USER_ID_TWO);
    }

    @Test
    void testGetUserByIdWhenUserNotExist() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(USER_ID));
        verify(userRepository).findById(USER_ID);
    }

    @Test
    void testGetUserByIdWhenUserExist() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userOne));

        User result = userService.getUserById(USER_ID);

        assertEquals(userOne.getId(), result.getId());
        verify(userRepository).findById(USER_ID);
    }

    @Test
    void testGetUsersByIdWhenUsersExists() {
        List<Long> usersId = List.of(USER_ID, USER_ID_TWO);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userOne));
        when(userRepository.findById(USER_ID_TWO)).thenReturn(Optional.of(userTwo));

        List<User> result = userService.getUsersById(usersId);

        assertEquals(userOne.getId(), result.get(0).getId());
        assertEquals(userTwo.getId(), result.get(1).getId());
        verify(userRepository, times(2)).findById(anyLong());
    }

    @Test
    void testUploadAvatarShouldUploadAndReturnProfilePic() {
        when(imageResizer.resizeMultipartImage(file, MAX_SIDE_SIZE)).thenReturn(resizedFile);
        when(imageResizer.resizeMultipartImage(file, MAX_SIDE_SIZE_MINI)).thenReturn(resizedMiniFile);
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userOne));
        when(s3Service.uploadFile(AVATAR_FOLDER, resizedFile)).thenReturn(FILE_ID);
        when(s3Service.uploadFile(AVATAR_MINI_FOLDER, resizedMiniFile)).thenReturn(MINI_FILE_ID);
        when(userRepository.save(userOne)).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfilePic result = userService.uploadAvatar(file);

        assertNotNull(result);
        assertEquals(result.getFileId(), FILE_ID);
        assertEquals(result.getSmallFileId(), MINI_FILE_ID);

        verify(userValidation).validateMaxFileSize(file);
        verify(userRepository).save(userOne);
    }

    @Test
    void testDeleteAvatarShouldRemoveFilesAndUnsetProfilePic() {
        UserProfilePic profilePic = new UserProfilePic();
        profilePic.setFileId(FILE_ID);
        profilePic.setSmallFileId(MINI_FILE_ID);
        userOne.setUserProfilePic(profilePic);
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userOne));

        userService.deleteAvatar();

        verify(s3Service).deleteFile(FILE_ID);
        verify(s3Service).deleteFile(MINI_FILE_ID);
        verify(userRepository).findById(USER_ID);
        assertEquals(null, userOne.getUserProfilePic());
    }

    @Test
    void downloadFileReturnsOriginalAvatar() {
        ByteArrayResource resource = new ByteArrayResource("data".getBytes());
        S3FileDto fileDto = S3FileDto.builder()
                .resource(resource)
                .fileName("avatar.jpg")
                .contentType(CONTENT_TYPE)
                .contentLength(resource.contentLength())
                .build();
        UserProfilePic profilePic = new UserProfilePic();
        profilePic.setFileId(FILE_ID);
        userOne.setUserProfilePic(profilePic);
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userOne));
        doNothing().when(userValidation).validateProfilePicNotNull(profilePic, USER_ID);
        when(s3Service.downloadFile(FILE_ID)).thenReturn(fileDto);

        S3FileDto result = userService.downloadFile();

        assertNotNull(result);
        assertEquals(fileDto.getFileName(), result.getFileName());
        assertEquals(fileDto.getContentType(), result.getContentType());
        assertEquals(fileDto.getContentLength(), result.getContentLength());
        assertEquals(fileDto.getResource(), result.getResource());

        verify(s3Service).downloadFile(FILE_ID);
    }

    @Test
    void testDownloadFileMiniReturnsMiniAvatar() {
        ByteArrayResource resource = new ByteArrayResource("miniData".getBytes());
        S3FileDto fileDto = S3FileDto.builder()
                .resource(resource)
                .fileName("avatar-mini.jpg")
                .contentType(CONTENT_TYPE)
                .contentLength(resource.contentLength())
                .build();

        UserProfilePic profilePic = new UserProfilePic();
        profilePic.setSmallFileId(MINI_FILE_ID);
        userOne.setUserProfilePic(profilePic);

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userOne));
        doNothing().when(userValidation).validateProfilePicNotNull(profilePic, USER_ID);
        when(s3Service.downloadFile(MINI_FILE_ID)).thenReturn(fileDto);

        S3FileDto result = userService.downloadFileMini();

        assertNotNull(result);
        assertEquals(fileDto.getFileName(), result.getFileName());
        assertEquals(fileDto.getContentType(), result.getContentType());
        assertEquals(fileDto.getContentLength(), result.getContentLength());
        assertEquals(fileDto.getResource(), result.getResource());

        verify(s3Service).downloadFile(MINI_FILE_ID);
    }
}