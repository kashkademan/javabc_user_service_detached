package school.faang.user_service.messages.redis.publishers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.events.EventStartDto;

@Slf4j
@Component
public class EventStartPublisher extends AbstractMessagePublisher<EventStartDto> {

    public EventStartPublisher(RedisTemplate<String, Object> redisTemplate,
                               @Qualifier(value = "eventStartTopic") ChannelTopic eventStartTopic) {
        super(redisTemplate, eventStartTopic);
    }
}