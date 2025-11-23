package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;
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
            log.info("Event{} published:", event);
        } catch (SerializationException e) {
            log.error("Serialization error while publishing event: {}", event, e);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis connection error while publishing event: {}", event, e);
        } catch (RedisSystemException e) {
            log.error("Redis system error while publishing event: {}", event, e);
        } catch (Exception e) {
            log.error("Unexpected error while publishing event: {}", event, e);
            throw new PublishingException(e.getMessage(), e);
        }


    }
}