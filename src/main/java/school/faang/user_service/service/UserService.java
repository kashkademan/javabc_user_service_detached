package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserException;
import school.faang.user_service.repository.UserRepository;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String USER_NOT_FOUND = "User by id={0} not found";

    private final UserRepository userRepository;

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(MessageFormat.format(USER_NOT_FOUND, userId)));
    }
}