package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.users.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final long USER_ID = 1L;
    private static final long USER_ID_TWO = 2L;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User userOne;
    private User userTwo;

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
}