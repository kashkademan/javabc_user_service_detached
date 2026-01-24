package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.FollowerEvent;

@Component
@RequiredArgsConstructor
public class FollowerEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.data.redis.channel.follower}")
    private String followerTopicName;

    public void publish(FollowerEvent event) {
        redisTemplate.convertAndSend(followerTopicName, event);
    }
}
