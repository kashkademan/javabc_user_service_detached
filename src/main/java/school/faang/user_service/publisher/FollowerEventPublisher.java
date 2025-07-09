package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.FollowerEvent;

@Component
public class FollowerEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    @Autowired
    public FollowerEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            @Qualifier("followerEventTopic") ChannelTopic topic
    ) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    public void publish(FollowerEvent event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
