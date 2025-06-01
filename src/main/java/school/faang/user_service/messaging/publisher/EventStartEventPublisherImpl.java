package school.faang.user_service.messaging.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.messaging.event.EventStartEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventStartEventPublisherImpl implements EventStartEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    @Override
    public void publish(EventStartEvent event) {
        try {
            redisTemplate.convertAndSend(topic.getTopic(), event);
        } catch (Exception e) {
            log.error("Не удалось опубликовать EventStartEvent для eventId: {}", event.getEventId(), e);
        }
    }
}
