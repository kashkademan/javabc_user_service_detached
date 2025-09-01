package school.faang.user_service.publisher;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.support.RetryTemplate;

/**
 * Тестовый наследник отправителя ивентов (используется {@link RecommendationEventPublisher})
 *
 * @author Linempy
 * @since 01.09.2025
 */
public class TestEventPublisher extends AbstractEventPublisher<RecommendationEventPublisher> {
    public TestEventPublisher(RetryTemplate retryTemplate,
                              RedisTemplate<String, Object> redisTemplate,
                              String topic) {
        super(retryTemplate, redisTemplate, topic);
    }
}