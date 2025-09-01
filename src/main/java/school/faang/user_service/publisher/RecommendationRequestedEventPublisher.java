package school.faang.user_service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestedEvent;

/**
 * Класс для отправки ивентов в топик с запросами рекомендаций
 *
 * @author Linempy
 * @since 13.08.2025
 */
@Slf4j
@Component
public class RecommendationRequestedEventPublisher extends AbstractEventPublisher<RecommendationRequestedEvent> {

    public RecommendationRequestedEventPublisher(RetryTemplate retryTemplate,
                                                 RedisTemplate<String, Object> redisTemplate,
                                                 @Value("${redis.topic.recommendation}") String topic) {
        super(retryTemplate, redisTemplate, topic);
    }





}