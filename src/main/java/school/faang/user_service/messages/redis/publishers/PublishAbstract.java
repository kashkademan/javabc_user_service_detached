package school.faang.user_service.messages.redis.publishers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
@Slf4j
public abstract class PublishAbstract {
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(ChannelTopic topic, Object message) {
        try {
            redisTemplate.convertAndSend(topic.getTopic(), message);
        } catch (Exception e) {
            log.error("Error to send {} topic", topic.getTopic());
        }
    }
}
