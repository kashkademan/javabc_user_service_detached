package school.faang.user_service.service.User;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.UserValidator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @InjectMocks
    private UserValidator userValidator;

    @Mock
    private UserRepository userRepository;

    @Test
    public void testCreaturesFalseById() {
        workWhenUserRepository(false);
        assertThrows(DataValidationException.class, () -> userValidator.validatorUserExistence(prepareDataUserDto().getId()));
    }

    @Test
    public void testIdealEvent() {
        workWhenUserRepository(true);
        assertDoesNotThrow(() -> userValidator.validatorUserExistence(prepareDataUserDto().getId()));
    }

    @Test
    public void testValidateNotSameUser(){
        long requesterId = 1L;
        long receiverId = 1L;
        assertThrows(DataValidationException.class,() -> userValidator.validateNotSameUser(requesterId, receiverId));
    }

    private void workWhenUserRepository(boolean result) {
        when(userRepository.existsById(prepareDataUserDto().getId())).thenReturn(result);
    }

    private @NotNull UserDto prepareDataUserDto() {
        return new UserDto(1L, "Name");
    }
}