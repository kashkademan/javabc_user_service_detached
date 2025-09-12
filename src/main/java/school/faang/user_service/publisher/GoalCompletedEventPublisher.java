package school.faang.user_service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.publish.GoalCompletedEventDto;
import school.faang.user_service.exception.EventPublishException;

import java.util.Objects;

@Component
@Slf4j
public class GoalCompletedEventPublisher implements MessagePublisher<GoalCompletedEventDto> {

    private final RedisTemplate<String, GoalCompletedEventDto> redisTemplate;
    private final ChannelTopic topic;

    public GoalCompletedEventPublisher(
            @Qualifier("goalCompletedRedisTemplate") RedisTemplate<String, GoalCompletedEventDto> redisTemplate,
            @Qualifier("goalCompletedTopic") ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(GoalCompletedEventDto goalCompletedEventDto) {
        Objects.requireNonNull(goalCompletedEventDto, "Event DTO must not be null");

        try {
            redisTemplate.convertAndSend(topic.getTopic(), goalCompletedEventDto);
            log.info("Successfully published event to topic {}: {}", topic.getTopic(), goalCompletedEventDto);
        } catch (SerializationException | DataAccessException e) {
            log.error("Failed to publish event to topic {}. Event: {}", topic.getTopic(), goalCompletedEventDto, e);
            throw new EventPublishException(
                    "Failed to publish message to topic %s".formatted(topic.getTopic()), e
            );
        }
    }
}
