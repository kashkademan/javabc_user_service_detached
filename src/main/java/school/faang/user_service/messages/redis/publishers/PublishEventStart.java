package school.faang.user_service.messages.redis.publishers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.events.EventStartDto;

@Slf4j
@Component
public class PublishEventStart extends PublishAbstract {
    private final ChannelTopic eventStartTopic;
    private final GenericJackson2JsonRedisSerializer genericJackson;

    public PublishEventStart(RedisTemplate<String, Object> redisTemplate, ChannelTopic eventStartTopic,
                             GenericJackson2JsonRedisSerializer genericJackson) {
        super(redisTemplate);
        this.eventStartTopic = eventStartTopic;
        this.genericJackson = genericJackson;
    }

    public void sendNotification(EventStartDto eventStartDto) {
        log.info("Start publish {} - event start", eventStartDto.eventId());
        Object json = genericJackson.serialize(eventStartDto);
        publish(eventStartTopic, json);
    }
}