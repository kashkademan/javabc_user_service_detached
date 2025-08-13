package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventStartEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventStartEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channel.event}")
    private String topicName;

    public void publish(EventStartEvent event) {
        redisTemplate.convertAndSend(topicName, event);
    }
}
