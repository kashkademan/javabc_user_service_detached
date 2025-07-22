package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.FollowerEventDto;

@Component
@RequiredArgsConstructor
@Slf4j
public class FollowerEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic followerTopic;

    public void publish(FollowerEventDto event) {
        try {
            redisTemplate.convertAndSend(followerTopic.getTopic(), event);
            log.info("Published FollowerEvent: {} to topic: {}", event, followerTopic.getTopic());
        } catch (Exception e) {
            log.error("Error publishing FollowerEvent: {} to topic: {}", event, followerTopic.getTopic(), e);
        }
    }
}