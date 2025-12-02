package school.faang.user_service.messages.redis.publishers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.exception.RedisPublishException;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractMessagePublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    public void publish(T message) {
        try {
            log.info("Publish event {}", message);
            redisTemplate.convertAndSend(topic.getTopic(), message);
        } catch (RedisPublishException e) {
            log.error("Error to send {} topic", topic.getTopic(), e.getCause());
        }
    }
}