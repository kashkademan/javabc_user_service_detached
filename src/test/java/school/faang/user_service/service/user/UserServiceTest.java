package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.repository.user.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(5L);
    }

    @Test
    public void testGetUserByIdOrThrow_successfully() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User returnUser = userService.getUserByIdOrThrow(user.getId());

        verify(userRepository, times(1)).findById(user.getId());
        assertEquals(user.getId(), returnUser.getId());
    }

    @Test
    public void testGetUserByIdOrThrow_userNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByIdOrThrow(user.getId()));
        verify(userRepository, times(1)).findById(user.getId());
    }
}
