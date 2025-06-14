package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserContext userContext;

    @Transactional(readOnly = true)
    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id {} not found", userId);
                    return new UserNotFoundException(userId);
                });
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        long userId = userContext.getUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id {} not found", userId);
                    return new UserNotFoundException(userId);
                });
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByIds(List<Long> userIds) {
        return userRepository.findAllById(userIds);
    }
}
