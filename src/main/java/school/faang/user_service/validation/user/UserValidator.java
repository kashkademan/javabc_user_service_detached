package school.faang.user_service.validation.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.user.UsernameAlreadyExistsException;
import school.faang.user_service.repository.user.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserValidator {
    private final UserRepository userRepository;

    public void checkExistsUsername(String username) {
        boolean existsUsername = userRepository.existsByUsername(username);
        if (existsUsername) {
            String errorMsg = String.format("Username %s already exists", username);
            log.error(errorMsg);
            throw new UsernameAlreadyExistsException(errorMsg);
        }
    }
}
