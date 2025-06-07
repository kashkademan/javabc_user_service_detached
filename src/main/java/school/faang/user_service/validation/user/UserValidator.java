package school.faang.user_service.validation.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.user.EmailAlreadyExistsException;
import school.faang.user_service.exception.user.PhoneAlreadyExistsException;
import school.faang.user_service.exception.user.UsernameAlreadyExistsException;
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
            String errorMsg = String.format("Username %s already exists", username);
            log.error(errorMsg);
            throw new UsernameAlreadyExistsException(errorMsg);
        }
    }

    private void checkExistsEmail(String email) {
        boolean existsUsername = userRepository.existsByEmail(email);
        if (existsUsername) {
            String errorMsg = String.format("Email %s already exists", email);
            log.error(errorMsg);
            throw new EmailAlreadyExistsException(errorMsg);
        }
    }

    private void checkExistsPhone(String phone) {
        boolean existsUsername = userRepository.existsByPhone(phone);
        if (existsUsername) {
            String errorMsg = String.format("Phone %s already exists", phone);
            log.error(errorMsg);
            throw new PhoneAlreadyExistsException(errorMsg);
        }
    }
}
