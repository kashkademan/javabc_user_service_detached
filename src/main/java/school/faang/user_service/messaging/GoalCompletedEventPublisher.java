package school.faang.user_service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.GoalCompletedEvent;
import school.faang.user_service.exception.DataValidationException;

@Component
@RequiredArgsConstructor
public class GoalCompletedEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.redis.channel.goal_completed}")
    private String channel;

    public void publish(GoalCompletedEvent goalCompletedEvent) {
        String json;
        try {
            json = objectMapper.writeValueAsString(goalCompletedEvent);
        } catch (JsonProcessingException e) {
            throw new DataValidationException("Event to JSON conversion error.");
        }
        redisTemplate.convertAndSend(channel, json);
    }
}