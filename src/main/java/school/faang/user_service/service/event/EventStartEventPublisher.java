package school.faang.user_service.service.event;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.EventStartEvent;

@Component
public class EventStartEventPublisher implements MessagePublisher {

    private RedisTemplate<String, Object> redisTemplate;

    public EventStartEventPublisher(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    @Autowired
    private ChannelTopic topic;

    public EventStartEventPublisher() {
    }

    public EventStartEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    public void publish(EventStartEvent event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
