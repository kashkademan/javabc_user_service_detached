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
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserAvatarUploadDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserProfilePic;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.ImageProcessing;
import school.faang.user_service.service.S3AvatarService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ImageProcessing imageProcessing;
    @Mock
    private S3AvatarService s3AvatarService;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(userService, "avatarMaxSizeMb", 5);
        ReflectionTestUtils.setField(userService, "allowedImageTypes",
                List.of("image/png", "image/jpeg", "image/jpg", "image/webp"));
        ReflectionTestUtils.setField(userService, "maxIdsPerRequest", 10);
    }

    @Test
    public void uploadAvatar_whenFileIsEmpty_shouldThrowDataValidationException() {
        when(userContext.getUserId()).thenReturn(1L);
        when(userRepository.getByIdOrThrow(1L)).thenReturn(new User());

        MockMultipartFile file = new MockMultipartFile("file", new byte[0]);
        UserAvatarUploadDto dto = new UserAvatarUploadDto(file);

        assertThrows(DataValidationException.class, () -> userService.uploadAvatar(dto));
    }

    @Test
    public void uploadAvatar_whenFileIsInvalidImage_shouldThrowDataValidationException() {
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
    public void uploadAvatar_whenNewAvatarProvided_shouldCreateProfilePic() throws IOException {
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
    public void uploadAvatar_whenExistingAvatarProvided_shouldDeleteOldAvatar() throws IOException {
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
    public void deleteAvatar_whenNoAvatar_shouldDoNothing() {
        when(userContext.getUserId()).thenReturn(1L);
        User user = new User();
        when(userRepository.getByIdOrThrow(1L)).thenReturn(user);

        assertDoesNotThrow(() -> userService.deleteAvatar());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void deleteAvatar_whenAvatarExists_shouldDeleteFilesAndClearProfilePic() {
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
    public void getAvatar_whenNoAvatar_shouldThrowDataValidationException() {
        User user = new User();
        when(userRepository.getByIdOrThrow(1L)).thenReturn(user);

        assertThrows(DataValidationException.class, () -> userService.getAvatar(1L, "big"));
    }

    @Test
    public void getAvatar_whenInvalidSize_shouldThrowDataValidationException() {
        UserProfilePic pic = new UserProfilePic();
        pic.setFileId("big");
        pic.setSmallFileId("small");
        User user = new User();
        user.setUserProfilePic(pic);
        when(userRepository.getByIdOrThrow(1L)).thenReturn(user);

        assertThrows(DataValidationException.class, () -> userService.getAvatar(1L, "invalid"));
    }

    @Test
    public void getAvatar_whenBigRequested_shouldReturnBigFile() {
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
    public void getAvatar_whenSmallRequested_shouldReturnSmallFile() {
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

    @Test
    void getById_whenUserNotFound_shouldThrowEntityNotFoundException() {
        Long userId = 1L;
        when(userRepository.getByIdOrThrow(userId))
                .thenThrow(new EntityNotFoundException(String.format("User %d not found", userId)));

        assertThrows(EntityNotFoundException.class, () -> userService.getById(userId));
        verify(userRepository).getByIdOrThrow(userId);
        verify(userMapper, never()).toUserDto(any());
    }

    @Test
    void getById_whenUserExists_shouldReturnUserDto() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        UserDto expectedDto = new UserDto(
                userId,
                "username",
                "email",
                "phone",
                "about",
                null,
                List.of()
        );

        when(userRepository.getByIdOrThrow(userId)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expectedDto);

        UserDto result = userService.getById(userId);

        assertEquals(expectedDto, result);
        verify(userRepository).getByIdOrThrow(userId);
        verify(userMapper).toUserDto(user);
    }

    @Test
    void getUsersByIds_whenValidIds_shouldReturnUserDtos() {
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        User user3 = new User();
        user3.setId(3L);

        UserDto dto1 = new UserDto(
                1L,
                "user1",
                "email1",
                "phone1",
                "about1",
                null,
                List.of()
        );
        UserDto dto2 = new UserDto(
                2L,
                "user2",
                "email2",
                "phone2",
                "about2",
                null,
                List.of()
        );
        UserDto dto3 = new UserDto(
                3L,
                "user3",
                "email3",
                "phone3",
                "about3",
                null,
                List.of()
        );

        List<Long> userIds = List.of(1L, 2L, 3L);

        when(userRepository.findAllById(userIds)).thenReturn(List.of(user1, user2, user3));
        when(userMapper.toUserDto(user1)).thenReturn(dto1);
        when(userMapper.toUserDto(user2)).thenReturn(dto2);
        when(userMapper.toUserDto(user3)).thenReturn(dto3);

        ReflectionTestUtils.setField(userService, "maxIdsPerRequest", 10);

        List<UserDto> result = userService.getUsersByIds(userIds);

        assertEquals(3, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
        assertEquals(dto3, result.get(2));
        verify(userRepository).findAllById(userIds);
    }

    @Test
    void getUsersByIds_whenTooManyIds_shouldThrowException() {
        List<Long> userIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L);
        ReflectionTestUtils.setField(userService, "maxIdsPerRequest", 10);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> userService.getUsersByIds(userIds));

        assertEquals("Max 10 ids allowed", exception.getMessage());
        verify(userRepository, never()).findAllById(any());
    }

    @Test
    void getUsersByIds_whenNullId_shouldThrowException() {
        List<Long> userIds = Arrays.asList(1L, null, 3L);
        ReflectionTestUtils.setField(userService, "maxIdsPerRequest", 10);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> userService.getUsersByIds(userIds));

        assertEquals("Invalid IDs found: [null]", exception.getMessage());
        verify(userRepository, never()).findAllById(any());
    }

    @Test
    void getUsersByIds_whenNegativeId_shouldThrowException() {
        List<Long> userIds = List.of(1L, -5L, 3L);
        ReflectionTestUtils.setField(userService, "maxIdsPerRequest", 10);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> userService.getUsersByIds(userIds));

        assertEquals("Invalid IDs found: [-5]", exception.getMessage());
        verify(userRepository, never()).findAllById(any());
    }

    @Test
    void getUsersByIds_whenZeroId_shouldThrowException() {
        List<Long> userIds = List.of(1L, 0L, 3L);
        ReflectionTestUtils.setField(userService, "maxIdsPerRequest", 10);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> userService.getUsersByIds(userIds));

        assertEquals("Invalid IDs found: [0]", exception.getMessage());
        verify(userRepository, never()).findAllById(any());
    }
  
    @Test
    public void create_whenPasswordTooShort_shouldThrowDataValidationException() {
        ReflectionTestUtils.setField(userService, "minPasswordLength", 8);

        CreateUserDto userDto = new CreateUserDto(
                "username", "email@test.com", "short", 1L
        );

        assertThrows(DataValidationException.class, () -> userService.create(userDto));
    }

    @Test
    public void update_whenUserNotOwner_shouldThrowForbiddenException() {
        UpdateUserDto userDto = new UpdateUserDto("newUsername", "new@email.com", "321",
                "new about", 14L, "City");

        when(userContext.getUserId()).thenReturn(1L);

        assertThrows(ForbiddenException.class, () -> userService.update(2L, userDto));
    }


    @Test
    public void getById_shouldReturnUserDto() {
        User user = new User();
        user.setId(1L);
        UserDto expectedDto = new UserDto(1L, "username", "email@test.com",
                "about", " ");

        when(userRepository.getByIdOrThrow(1L)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expectedDto);

        UserDto result = userService.getById(1L);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(userRepository).getByIdOrThrow(1L);
    }
}
