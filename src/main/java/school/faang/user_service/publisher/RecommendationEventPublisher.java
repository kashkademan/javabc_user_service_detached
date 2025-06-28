package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.RecommendationEvent;

@Component
@RequiredArgsConstructor
public class RecommendationEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @SneakyThrows
    public void publish(RecommendationEvent event) {
        redisTemplate.convertAndSend("recommendation_topic", event);
    }
}
