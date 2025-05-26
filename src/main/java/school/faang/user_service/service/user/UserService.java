package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.user.UserNotFoundException;
import school.faang.user_service.model.redis.ActionType;
import school.faang.user_service.model.redis.TrackActionScore;
import school.faang.user_service.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    
    @Transactional(readOnly = true)
    @TrackActionScore(ActionType.OPEN_PROFILE)
    public User getUserByIdOrThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id {} not found", userId);
                    return new UserNotFoundException(userId);
                });
    }
}
