package school.faang.user_service.validation.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.user.UserAlreadyExistsException;
import school.faang.user_service.repository.user.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserValidator {
    private final UserRepository userRepository;

    public void validateUser(User user) {
        checkExistsUsername(user.getUsername());
        checkExistsEmail(user.getEmail());
        checkExistsPhone(user.getPhone());
    }

    private void checkExistsUsername(String username) {
        boolean existsUsername = userRepository.existsByUsername(username);
        if (existsUsername) {
            log.error("Username {} already exists", username);
            throw new UserAlreadyExistsException(UserAlreadyExistsException.UserField.USERNAME, username);
        }
    }

    private void checkExistsEmail(String email) {
        boolean existsUsername = userRepository.existsByEmail(email);
        if (existsUsername) {
            log.error("Email {} already exists", email);
            throw new UserAlreadyExistsException(UserAlreadyExistsException.UserField.EMAIL, email);
        }
    }

    private void checkExistsPhone(String phone) {
        boolean existsUsername = userRepository.existsByPhone(phone);
        if (existsUsername) {
            log.error("Phone {} already exists", phone);
            throw new UserAlreadyExistsException(UserAlreadyExistsException.UserField.PHONE, phone);
        }
    }
}
