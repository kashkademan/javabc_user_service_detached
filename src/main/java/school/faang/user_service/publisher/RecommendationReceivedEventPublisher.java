package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationReceivedEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class RecommendationReceivedEventPublisher implements MessagePublisher {

    @Value("${spring.data.redis.topics.recommendation}")
    private String recommendationChannelName;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(RecommendationReceivedEvent event) {
        try {
            redisTemplate.convertAndSend(recommendationChannelName, event);
            log.info("Recommendation event published: eventId={}", event.recommendationId());

        } catch (RuntimeException e) {
            log.error("Failed to publish recommendation event: {}", e.getMessage(), e);
        }
    }
}