package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.authorization.UserUnauthorizedException;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.country.CountryService;
import school.faang.user_service.service.image.ImageServiceTest;
import school.faang.user_service.validation.user.UserValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private UserValidator userValidator;
    @Mock
    private CountryService countryService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ImageServiceTest imageService;
    @Mock
    private ApplicationContext applicationContext;
    @InjectMocks
    private UserService userService;
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(5L);
    }

    @Test
    public void testGetUserById_successfully() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User returnUser = userService.getUserById(user.getId());

        verify(userRepository, times(1)).findById(user.getId());
        assertEquals(user.getId(), returnUser.getId());
    }

    @Test
    public void testGetUserById_userNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(user.getId()));
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    public void testGetCurrentUser_successfully() {
        when(userContext.getUserId()).thenReturn(user.getId());
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User returnUser = userService.getCurrentUser();

        verify(userRepository, times(1)).findById(eq(user.getId()));
        assertEquals(user.getId(), returnUser.getId());
    }

    @Test
    public void testGetCurrentUser_userInContextNotFound() {
        when(userContext.getUserId()).thenThrow(UserUnauthorizedException.class);

        assertThrows(UserUnauthorizedException.class, () -> userService.getCurrentUser());
        verify(userRepository, never()).findById(eq(user.getId()));
    }

    @Test
    public void testGetCurrentUser_userNotFound() {
        when(userContext.getUserId()).thenReturn(user.getId());
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getCurrentUser());
        verify(userRepository, times(1)).findById(eq(user.getId()));
    }

    @Test
    public void testGetUsersByIds() {
        List<Long> userIds = List.of(user.getId());
        when(userRepository.findAllById(userIds)).thenReturn(List.of(user));

        List<User> returnUsers = userService.getUsersByIds(userIds);

        verify(userRepository, times(1)).findAllById(eq(userIds));
        assertEquals(userIds, returnUsers.stream().map(User::getId).toList());
        assertEquals(userIds.size(), returnUsers.size());
    }
}
