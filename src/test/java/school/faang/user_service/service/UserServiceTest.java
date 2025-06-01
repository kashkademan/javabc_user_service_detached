package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.FileData;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.FileSizeException;
import school.faang.user_service.exception.InvalidImageFormatException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.CsvMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.publisher.AuthorResponseEventPublisher;
import school.faang.user_service.publisher.ProfileViewEventPublisher;
import school.faang.user_service.publisher.SkillAcquiredEventPublisher;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@NoArgsConstructor
public class UserServiceTest {

    private final Long id = 1L;
    private final byte[] imageData = new byte[]{0x1, 0x2, 0x3};
    private final List<Long> ids = List.of(1L, 2L, 3L);

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserContext userContext;

    @Mock
    private S3StorageService s3Service;

    @Mock
    private ImageCompressorService compressorService;

    @Mock
    private SkillAcquiredEventPublisher skillAcquiredEventPublisher;

    @Mock
    private GoalMapper goalMapper;
    @Mock
    private GoalService goalService;
    @Mock
    private EventService eventService;
    @Mock
    private MentorshipService mentorshipService;
    @Mock
    private ProfileViewEventPublisher profileViewEventPublisher;

    @Mock
    CountryRepository countryRepository;

    @Mock
    PasswordService passwordService;

    @Spy
    CsvMapper csvMapper;

    @InjectMocks
    private UserService userService;

    @Mock
    private AuthorResponseEventPublisher authorResponseEventPublisher;

    @BeforeEach
    void setUp() {
        userService = new UserService(
                userRepository,
                csvMapper,
                userMapper,
                goalMapper,
                goalService,
                eventService,
                mentorshipService,
                countryRepository,
                passwordService,
                userContext,
                s3Service,
                compressorService,
                skillAcquiredEventPublisher,
                profileViewEventPublisher,
                authorResponseEventPublisher
        );
    }

    @Test
    @DisplayName("Negative: error when user not found")
    void testFindUserNegativeNoUser() {
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        var exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.findUserById(id)
        );

        assertEquals(exception.getMessage(), String.format("User with id = %d doesn't exist", id));
    }

    @Test
    @DisplayName("Positive: successful find user by id")
    void testFindUserSuccess() {
        User user = createUser(id);
        UserDto userDto = createUserDto(id);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);
        UserDto result = userService.findUserById(user.getId());

        assertEquals(userDto, result);
        assertEquals(user.getId(), result.id());
    }

    @Test
    @DisplayName("Negative: no users found by ids")
    void testGetUsersByIdsNegativeNoUsers() {
        when(userRepository.findAllById(ids)).thenReturn(Collections.emptyList());
        List<UserDto> users = userService.getUsersByIds(ids);

        assertEquals(0, users.size());
    }

    @Test
    @DisplayName("Positive: successful find users by ids")
    void testGetUsersByIdsSuccess() {
        List<User> users = createUsers(ids);
        List<UserDto> userDto = createUserDtos(users);

        when(userRepository.findAllById(ids)).thenReturn(users);
        when(userMapper.toDto(any(User.class))).thenAnswer(input -> {
            User user = input.getArgument(0);
            return createUserDto(user.getId());
        });
        List<UserDto> result = userService.getUsersByIds(ids);

        assertEquals(userDto, result);
    }

    @Test
    public void positiveRegisterUsersFromFile() {
        MockMultipartFile file;
        file = createValidCsvFile();
        when(passwordService.generateRandomPassword(anyInt()))
                .thenReturn("mockedPassword123");

        User savedUser = new User();
        savedUser.setUsername("John Doe");
        savedUser.setEmail("johndoe@example.com");

        UserDto expectedUserDto = UserDto.builder()
                .username("John Doe")
                .email("johndoe@example.com")
                .build();

        when(userMapper.toDto(any(User.class))).thenReturn(expectedUserDto);
        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        List<UserDto> result = userService.registerUserFromFile(file);

        UserDto userDto = result.get(0);

        //verify(passwordService).generateRandomPassword(10);
        verify(userRepository).save(any(User.class));

        assertEquals("John Doe", userDto.username());
        assertEquals("johndoe@example.com", userDto.email());
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void negativeRegisterUsersEmptyFile() {
        String csvContent = """
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "users.csv",
                "text/csv",
                csvContent.getBytes()
        );

        assertThrows(DataValidationException.class, () -> userService.registerUserFromFile(file));
    }

    @Test
    public void negativeRegisterUsersIncorrectDataLength() {
        String csvContent = "username,email,age\nJohn Doe,john@example.com,30";
        MockMultipartFile file = new MockMultipartFile("file", "users.csv",
                "text/csv", csvContent.getBytes());

        CompletionException completionException = assertThrows(
                CompletionException.class,
                () -> userService.registerUserFromFile(file).wait()
        );

        assertInstanceOf(ArrayIndexOutOfBoundsException.class, completionException.getCause());
    }


    @Test
    void testNegativeCreateAvatarWhenFileTypeIncorrect() {
        MockMultipartFile imageFile = createImageFile("image/pdf", imageData);

        assertThrows(InvalidImageFormatException.class, () -> userService.createUserAvatar(imageFile));
    }

    @Test
    void testNegativeCreateAvatarWhenFileSizeIncorrect() {
        byte[] bytes = new byte[6 * 1024 * 1024];
        Arrays.fill(bytes, (byte) 0x1);
        MockMultipartFile imageFile = createImageFile("image/jpeg", bytes);

        assertThrows(FileSizeException.class, () -> userService.createUserAvatar(imageFile));
    }

    @Test
    void testNegativeCreateAvatarWhenUserNotFound() {
        MockMultipartFile imageFile = createImageFile("image/jpeg", imageData);

        assertThrows(RuntimeException.class, () -> userService.createUserAvatar(imageFile));
    }

    @Test
    void testPositiveCreateAvatar() {
        MockMultipartFile imageFile = createImageFile("image/jpeg", imageData);
        when(userContext.getUserId()).thenReturn(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(createUser(id)));

        userService.createUserAvatar(imageFile);

        ArgumentCaptor<Integer> sizeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(userRepository, times(1)).save(any(User.class));
        verify(compressorService, times(2)).compressImage(
                any(MultipartFile.class),
                sizeCaptor.capture()
        );
        assertTrue(sizeCaptor.getAllValues().containsAll(List.of(1080, 170)));
    }

    @Test
    void testNegativeGetAvatarWhenAvatarNotFound() {
        assertThrows(EntityNotFoundException.class, () -> userService.getUserAvatar(id));
    }

    @Test
    void testPositiveGetAvatar() throws IOException {
        String expectedFileName = "file";
        InputStream fixedInputStream = new ByteArrayInputStream(imageData);
        User user = createUserWithAvatar(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(s3Service.getFile(expectedFileName)).thenReturn(fixedInputStream);
        when(s3Service.getContentType(expectedFileName)).thenReturn("image/jpeg");

        FileData resultFile = userService.getUserAvatar(id);

        byte[] resultBytes = toByteArray(resultFile.content());
        assertArrayEquals(imageData, resultBytes);
        assertEquals("image/jpeg", resultFile.contentType());
    }

    @Test
    void testPositiveRemoveAvatar() {
        User user = createUserWithAvatar(id);
        when(userContext.getUserId()).thenReturn(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.removeUserAvatar();

        verify(s3Service, times(2)).deleteFile(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Positive: successful activate user")
    void shouldActivateUserIfUpdatedRecently() {
        Long id = 1L;
        User user = new User();
        user.setId(id);
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now().minusMonths(1));

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDto dto = UserDto.builder().id(id).username("gor").email("gor@x.com").active(true).build();
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDto result = userService.activateUser(id);

        verify(userRepository).save(user);
        assertTrue(result.active());
    }

    @Test
    @DisplayName("Positive: successful deactivate user")
    void shouldDeactivateUser() {
        Long id = 1L;
        User user = new User();
        user.setId(id);
        user.setActive(true);
        user.setGoals(List.of());

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(
                UserDto.builder().id(id).username("deact").email("d@x.com").active(false).build()
        );
        when(eventService.getParticipatedEvents(id)).thenReturn(List.of());

        UserDto result = userService.deactivateUser(id);

        verify(goalService).deleteAllByIds(any());
        verify(goalService).removeUserFromGoals(any(), eq(id));
        verify(eventService).deleteAllByIds(any());
        verify(eventService).removeUserFromEvents(any(), eq(id));
        verify(mentorshipService).deleteMentorship(id);
        assertFalse(result.active());
    }

    @Test
    @DisplayName("Positive: successful stop user goals")
    void shouldStopUserGoalsCorrectly() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Goal goal1 = new Goal();
        Goal goal2 = new Goal();
        user.setGoals(List.of(goal1, goal2));

        GoalDto deletableGoal = GoalDto.builder()
                .id(101L)
                .title("Deletable Goal")
                .userIds(List.of(userId))
                .build();

        GoalDto removableGoal = GoalDto.builder()
                .id(102L)
                .title("Shared Goal")
                .userIds(List.of(userId, 2L))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(goalMapper.goalToGoalDto(goal1)).thenReturn(deletableGoal);
        when(goalMapper.goalToGoalDto(goal2)).thenReturn(removableGoal);
        when(eventService.getParticipatedEvents(userId)).thenReturn(List.of());

        when(userMapper.toDto(any())).thenReturn(UserDto.builder()
                .id(userId)
                .username("test")
                .email("t@t.com")
                .active(false)
                .build());

        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        userService.deactivateUser(userId);

        verify(goalService).deleteAllByIds(List.of());
        verify(goalService).removeUserFromGoals(List.of(102L, 102L), userId);
    }

    @Test
    void testNegativeBanUserWhenUserNotFound() {
        assertThrows(UserNotFoundException.class, () -> userService.banUser(id));
    }

    @Test
    void testPositiveBanUser() {
        User user = createUser(id);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.banUser(id);

        verify(userRepository, times(1)).save(user);
    }

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private List<User> createUsers(List<Long> ids) {
        return ids.stream()
                .map(this::createUser)
                .toList();
    }

    private UserDto createUserDto(Long id) {
        return UserDto.builder()
                .id(id)
                .username("test")
                .email("test")
                .active(true)
                .build();
    }

    private List<UserDto> createUserDtos(List<User> users) {
        return users.stream()
                .map(user -> createUserDto(user.getId()))
                .toList();
    }

    private MockMultipartFile createImageFile(String contentType, byte[] bytes) {
        return new MockMultipartFile(
                "file",
                "avatar.jpg",
                contentType,
                bytes
        );
    }

    private User createUserWithAvatar(Long id) {
        return User.builder()
                .id(id)
                .userProfilePic(UserProfilePic.builder()
                        .fileId("file")
                        .smallFileId("smallfile")
                        .build())
                .build();
    }

    private byte[] toByteArray(InputStream inputStream) throws IOException {
        try (inputStream) {
            return inputStream.readAllBytes();
        }
    }

    private MockMultipartFile createValidCsvFile() {
        String csvContent = """
                John,Doe,1998,A,123456,johndoe@example.com,+1-123-456-7890,123 Main Street,New York,NY,USA,10001,Computer Science,3,Software Engineering,3.8,Active,2016-09-01,2020-05-30,High School Diploma,XYZ High School,2016,true,XYZ Technologies;
                """;

        return new MockMultipartFile(
                "file",
                "valid_users.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );
    }

}
