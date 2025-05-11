package school.faang.user_service.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
public class RedisEventPublisher<T> implements EventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    @Override
    public void publish(T event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
