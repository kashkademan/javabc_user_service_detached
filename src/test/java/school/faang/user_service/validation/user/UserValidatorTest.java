package school.faang.user_service.validation.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.user.UserAlreadyExistsException;
import school.faang.user_service.repository.user.UserRepository;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("uniqueUser");
        user.setEmail("unique@example.com");
        user.setPhone("+70000000000");
    }

    @Test
    void testValidateUser_successfully() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByPhone(user.getPhone())).thenReturn(false);

        assertDoesNotThrow(() -> userValidator.validateUser(user));
    }

    @Test
    void testValidateUser_usernameExists() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userValidator.validateUser(user)
        );

        assertEquals(UserAlreadyExistsException.UserField.USERNAME, exception.getField());
    }

    @Test
    void testValidateUser_emailExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);


        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userValidator.validateUser(user)
        );

        assertEquals(UserAlreadyExistsException.UserField.EMAIL, exception.getField());
    }

    @Test
    void testValidateUser_phoneExists() {
        when(userRepository.existsByPhone(user.getPhone())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userValidator.validateUser(user)
        );

        assertEquals(UserAlreadyExistsException.UserField.PHONE, exception.getField());
    }
}