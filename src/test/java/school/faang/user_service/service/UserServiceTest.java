package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.config.MinioService;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFullDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.event.ProfilePicEvent;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.publisher.ProfilePicEventPublisher;
import school.faang.user_service.repository.UserRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapperImpl userMapper;
    @Mock
    private CountryService countryService;
    @Mock
    private MinioService minioService;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ProfilePicEventPublisher eventPublisher;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "diceBearApi", "http://example.com/api");
    }

    @Test
    void newUserTestIncorrectUsername() {
        UserFullDto dto = createDto("123", "qw@qwer.ru", "22222222222", -1, null);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
    }

    @Test
    void newUserTestIncorrectEmail() {
        UserFullDto dto = createDto("qwe", "qwqwerru", "22222222222", 1, null);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
    }

    @Test
    void newUserTestIncorrectPhone() {
        UserFullDto dto = createDto("qwe", "qw@qwer.ru", "2", 2, null);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
    }

    @Test
    void newUserTestIncorrectExperience() {
        UserFullDto dto = createDto("qwe", "qw@qwer.ru", "22222222222", -1, null);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
    }

    @Test
    void newUserTestFilterIsNull() {
        Long userId = 1L;
        UserFullDto dto = createDto("qwe", "qw@qwer.ru", "22222222222", 1, null);
        Country country = new Country();
        String testApi = "http://example.com/api";
        String file = "test file";
        User user = User.builder()
                .id(userId)
                .build();

        when(countryService.getCountryById(any())).thenReturn(country);
        when(restTemplate.getForObject(testApi, String.class)).thenReturn(file);
        when(userRepository.save(any())).thenReturn(user);

        UserDto result = userService.createUser(dto);

        assertNotNull(result);
        assertEquals(userId, result.id());
    }

    @Test
    void newUserTestFilterIsNotNull() {
        Long userId = 1L;
        UserFullDto dto = createDto("qwe", "qw@qwer.ru", "22222222222", 1, "someone");
        Country country = new Country();
        String testApi = "http://example.com/api?someone";
        String file = "test file";
        User user = User.builder()
                .id(userId)
                .build();

        when(countryService.getCountryById(any())).thenReturn(country);
        when(restTemplate.getForObject(testApi, String.class)).thenReturn(file);
        when(userRepository.save(any())).thenReturn(user);

        UserDto result = userService.createUser(dto);

        assertNotNull(result);
        assertEquals(userId, result.id());
    }

    @Test
    void getUserByIdException() {
        long id = -1L;
        Mockito.when(userRepository.findById(id))
                .thenThrow(new IllegalArgumentException("The Requester with id =" + id + " does not exist"));

        assertThrows(IllegalArgumentException.class, () -> userService.getUserById(id));
    }

    @Test
    void getUserById() {
        long id = 1L;
        User user = User.builder()
                .id(id)
                .build();

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User result = userService.getUserById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void updateUserProfilePictureWhenUserExistsShouldUpdatePicture() {
        Long userId = 1L;
        String newFileId = "new_file.jpg";
        String newSmallFileId = "new_small_file.jpg";

        User user = User.builder()
                .id(userId)
                .userProfilePic(new UserProfilePic())
                .build();

        UserDto expectedDto = new UserDto(userId, "test", "test@test.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserDto result = userService.updateUserProfilePicture(userId, newFileId, newSmallFileId);

        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals(newFileId, user.getUserProfilePic().getFileId());
        assertEquals(newSmallFileId, user.getUserProfilePic().getSmallFileId());

        verify(eventPublisher).publish(any(ProfilePicEvent.class));
    }

    @Test
    void updateUserProfilePictureWhenUserHasNoPreviousPictureShouldCreateNewPicture() {
        Long userId = 1L;
        String newFileId = "new_file.jpg";
        String newSmallFileId = "new_small_file.jpg";

        User user = User.builder()
                .id(userId)
                .userProfilePic(null)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        userService.updateUserProfilePicture(userId, newFileId, newSmallFileId);

        assertNotNull(user.getUserProfilePic());
        assertEquals(newFileId, user.getUserProfilePic().getFileId());
        assertEquals(newSmallFileId, user.getUserProfilePic().getSmallFileId());
    }

    @Test
    void updateUserProfilePictureWhenUserNotFoundShouldThrowException() {
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.updateUserProfilePicture(userId, "new.jpg", "new_small.jpg"));
    }

    @Test
    void updateUserProfilePictureShouldPublishCorrectEvent() {
        Long userId = 1L;
        String oldFileId = "old.jpg";
        String oldSmallFileId = "old_small.jpg";
        final String newFileId = "new.jpg";
        final String newSmallFileId = "new_small.jpg";

        UserProfilePic existingPic = new UserProfilePic();
        existingPic.setFileId(oldFileId);
        existingPic.setSmallFileId(oldSmallFileId);

        User user = User.builder()
                .id(userId)
                .userProfilePic(existingPic)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        userService.updateUserProfilePicture(userId, newFileId, newSmallFileId);

        verify(eventPublisher).publish(argThat(event ->
                event.getUserId().equals(userId)
                        && event.getNewFileId().equals(newFileId)
                        && event.getNewSmallFileId().equals(newSmallFileId)
                        && event.getOldFileId().equals(oldFileId)
                        && event.getOldSmallFileId().equals(oldSmallFileId)
                        && event.getChangedAt() != null
        ));
    }

    private UserFullDto createDto(String username, String email, String phone,
                                  Integer experience, String defaultPhoto) {
        return UserFullDto.builder()
                .username(username)
                .email(email)
                .phone(phone)
                .experience(experience)
                .defaultPhoto(defaultPhoto)
                .build();
    }
}