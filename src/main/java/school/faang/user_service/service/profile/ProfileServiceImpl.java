package school.faang.user_service.service.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.ProfileViewEvent;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.publisher.ProfileViewEventPublisher;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileViewEventPublisher profileViewEventPublisher;
    private final UserContext userContext;
    private final UserService userService;

    @Override
    public UserDto getProfile(long userId) {
        profileViewEventPublisher.publish(new ProfileViewEvent(userContext.getUserId(), userId, LocalDateTime.now()));
        return userService.getUser(userId);
    }
}
