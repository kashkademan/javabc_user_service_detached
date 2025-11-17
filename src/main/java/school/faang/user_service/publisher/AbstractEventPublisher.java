package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.event.SearchAppearanceEvent;
import school.faang.user_service.exception.PublishingException;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> implements MessagePublisher<T> {

    private final String channelName;
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(SearchAppearanceEvent event) {
        try {
            redisTemplate.convertAndSend(channelName, event);

            log.info("Event published: eventId={}", event.getClass().getName());

        } catch (Exception e) {
            log.error("Failed to publish event: {}", e.getMessage(), e);
            throw new PublishingException("Failed to publish event", e);
        }
    }
}