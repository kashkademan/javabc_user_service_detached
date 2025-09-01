package school.faang.user_service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationEvent;

/**
 * Класс-отправитель ивента {@link RecommendationEvent} в сервис аналитики
 *
 * @author Linempy
 * @since 20.08.2025
 */
@Slf4j
@Component
public class RecommendationEventPublisher extends AbstractEventPublisher<RecommendationEvent> {

    public RecommendationEventPublisher(RetryTemplate retryTemplate,
                                        RedisTemplate<String, Object> redisTemplate,
                                        @Value("${redis.topic.recommendation}") String topic) {
        super(retryTemplate, redisTemplate, topic);
    }
}