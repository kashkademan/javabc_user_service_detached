package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;

import static school.faang.user_service.utils.Utils.format;

@Service
@RequiredArgsConstructor
public class UserService {
    public static final String USER_NOT_FOUND = "User by ID={} is not found";

    private final UserRepository userRepository;

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(format(USER_NOT_FOUND, userId)));
    }
}