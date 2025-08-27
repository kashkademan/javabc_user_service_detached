package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestedEvent;
import school.faang.user_service.exception.EventPublishingException;

/**
 * Класс для отправки ивентов в топик с запросами рекомендаций
 *
 * @author Linempy
 * @since 13.08.2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationRequestedEventPublisher implements EventPublisher<RecommendationRequestedEvent> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${redis.topic.recommendation-request}")
    private String recommendationRequestTopic;

    public void publish(RecommendationRequestedEvent event) {
        try {
            Long receiversCount = redisTemplate.convertAndSend(recommendationRequestTopic, event);

            if (receiversCount != null && receiversCount > 0) {
                log.info("Событие успешно отправлено в топик {}. Получателей: {}",
                        recommendationRequestTopic, receiversCount);
            } else {
                log.warn("Событие отправлено в топик {}, но нет активных подписчиков",
                        recommendationRequestTopic);
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке события в топик {}: {}",
                    recommendationRequestTopic, event, e);
            throw new EventPublishingException("Ошибка отправки ивента в Redis", e);
        }
    }
}