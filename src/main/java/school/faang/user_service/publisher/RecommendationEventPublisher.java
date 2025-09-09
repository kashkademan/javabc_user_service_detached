package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.publish.RecommendationEventDto;

@Component
@Primary
@RequiredArgsConstructor
public class RecommendationEventPublisher implements MessagePublisher<RecommendationEventDto> {
    @Value("${spring.data.redis.channel.recommendation-channel}")
    private String redisRecommendationEventTopic;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(RecommendationEventDto recommendationEventDto) {
        try {
            String json = objectMapper.writeValueAsString(recommendationEventDto);
            redisTemplate.convertAndSend(redisRecommendationEventTopic, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("An error occurred while sending a message to the topic %s",
                    redisRecommendationEventTopic), e);
        }
    }
}
