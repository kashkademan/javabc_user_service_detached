package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.resource.S3FileDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.exception.users.UserNotFoundException;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.avatar.AvatarGeneratorService;
import school.faang.user_service.service.image.ImageResizer;
import school.faang.user_service.service.s3.S3Service;
import school.faang.user_service.validation.file.FileValidation;
import school.faang.user_service.validation.user.UserValidation;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
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
    private FileValidation fileValidation;
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
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AvatarGeneratorService avatarGeneratorService;
    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private UserService userService;

    private User userOne;
    private User userTwo;
    private User userToCreate;
    private Country country;
    private Long countryId;
    private String rawPassword;
    private String encodedPassword;
    private String avatarPath;

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

        countryId = 1L;
        rawPassword = "rawPassword123";
        encodedPassword = "encodedPasswordABC";
        avatarPath = "/avatars/some_unique_id.png";

        userToCreate = new User();
        userToCreate.setUsername("newUser");
        userToCreate.setEmail("newuser@example.com");
        userToCreate.setPassword(rawPassword);

        country = new Country();
        country.setId(countryId);
        country.setTitle("Wonderland");
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

        verify(fileValidation).validateMaxFileSize(file);
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
        assertNull(userOne.getUserProfilePic());
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
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userOne));
        doNothing().when(userValidation).validateProfilePicNotNull(profilePic, USER_ID);
        when(s3Service.downloadFile(FILE_ID)).thenReturn(fileDto);

        S3FileDto result = userService.downloadFile(USER_ID);

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
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userOne));
        doNothing().when(userValidation).validateProfilePicNotNull(profilePic, USER_ID);
        when(s3Service.downloadFile(MINI_FILE_ID)).thenReturn(fileDto);

        S3FileDto result = userService.downloadFileMini(USER_ID);

        assertNotNull(result);
        assertEquals(fileDto.getFileName(), result.getFileName());
        assertEquals(fileDto.getContentType(), result.getContentType());
        assertEquals(fileDto.getContentLength(), result.getContentLength());
        assertEquals(fileDto.getResource(), result.getResource());
        verify(s3Service).downloadFile(MINI_FILE_ID);
    }

    @Test
    void testCreateUserWithProperValues() {
        when(avatarGeneratorService.generateAndUpload())
                .thenReturn(CompletableFuture.completedFuture(avatarPath));
        when(countryRepository.findById(countryId))
                .thenReturn(Optional.of(country));
        when(passwordEncoder.encode(rawPassword))
                .thenReturn(encodedPassword);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User savedUser = invocation.getArgument(0, User.class);
                    savedUser.setId(100L);
                    return savedUser;
                });

        User resultUser = userService.createUser(userToCreate, countryId);

        assertNotNull(resultUser);
        assertEquals(100L, resultUser.getId());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertNotNull(capturedUser.getUserProfilePic());
        assertEquals(avatarPath, capturedUser.getUserProfilePic().getSmallFileId());
        assertEquals(country, capturedUser.getCountry());
        assertEquals(encodedPassword, capturedUser.getPassword());
        assertTrue(capturedUser.isActive());

        verify(avatarGeneratorService).generateAndUpload();
        verify(countryRepository).findById(countryId);
        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    void testCreateUserWhenCountryNotFound() {
        when(avatarGeneratorService.generateAndUpload())
                .thenReturn(CompletableFuture.completedFuture(avatarPath));
        when(countryRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                userService.createUser(userToCreate, countryId),
                "Country with id " + countryId + "is not found");


                verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateUserWhenAvatarGenerationFails() {
        CompletableFuture<String> failedFuture = CompletableFuture.failedFuture(
                new ExecutionException("S3 is down", null)
        );

        when(avatarGeneratorService.generateAndUpload()).thenReturn(failedFuture);
        when(countryRepository.findById(countryId)).thenReturn(Optional.of(country));
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, User.class));

        userService.createUser(userToCreate, countryId);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertNotNull(capturedUser.getUserProfilePic());
        assertNull(capturedUser.getUserProfilePic().getSmallFileId());
        assertEquals(country, capturedUser.getCountry());
        assertEquals(encodedPassword, capturedUser.getPassword());
        assertTrue(capturedUser.isActive());
    }
}