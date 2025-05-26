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
import static school.faang.user_service.utils.Utils.format;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("testing get existing user entity by id")
    public void testGetExistUserById() {
        Long userId = 1L;
        User user = getUser(userId);
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        User resultUser = userService.getUserById(userId);

        assertNotNull(resultUser);
        assertEquals("Name", resultUser.getUsername());
    }

    @Test
    @DisplayName("testing get NOT existing user entity by id")
    public void testGetAnAbsentUserById() {
        Long userId = 1L;
        String expected = format(UserService.USER_NOT_FOUND, userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException result = assertThrows(
                UserNotFoundException.class, () -> userService.getUserById(1L));
        assertEquals(expected, result.getMessage());
    }

    private User getUser(Long userId) {
        return User.builder()
                .id(userId)
                .username("Name")
                .build();
    }
}