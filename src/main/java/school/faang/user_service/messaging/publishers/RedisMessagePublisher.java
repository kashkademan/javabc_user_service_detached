package school.faang.user_service.messaging.publishers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisMessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisMessagePublisher(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void publishMessage(String topic, Object message) {
        redisTemplate.convertAndSend(topic, message);
    }
}