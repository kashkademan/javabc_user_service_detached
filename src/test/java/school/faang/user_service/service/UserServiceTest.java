package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final Long USER_ID = 1L;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("testing get existing user entity by id")
    public void testGetExistUserById() {
        User user = getUser();
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        User resultUser = userService.getUserById(USER_ID);

        assertNotNull(resultUser);
        assertEquals("Name", resultUser.getUsername());
    }

    @Test
    @DisplayName("testing get NOT existing user entity by id")
    public void testGetAnAbsentUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    private User getUser() {
        return User.builder()
                .id(USER_ID)
                .username("Name")
                .build();
    }
}