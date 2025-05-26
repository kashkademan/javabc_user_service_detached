package school.faang.user_service.jobs;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.user.UserService;

@Component
@RequiredArgsConstructor
public class UserScoreJob{
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Scheduled
    protected void executeInternal() {

    }
}
