package school.faang.user_service.event.follower;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FollowerEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic followerTopic;

    public void publish(FollowerEvent followerEvent) {
        redisTemplate.convertAndSend(followerTopic.getTopic(), followerEvent);
    }
}
