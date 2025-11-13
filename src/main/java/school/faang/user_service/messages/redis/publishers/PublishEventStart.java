package school.faang.user_service.messages.redis.publishers;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.events.EventStartDto;

@Component
public class PublishEventStart extends PublishAbstract {

    public PublishEventStart(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    public void sendNotification(EventStartDto eventStartDto) {

    }
}