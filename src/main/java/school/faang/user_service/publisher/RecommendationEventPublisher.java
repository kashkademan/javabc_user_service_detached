package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationEvent;
import school.faang.user_service.exception.EventPublishingException;

/**
 * Класс-отправитель ивента {@link RecommendationEvent} в сервис аналитики
 *
 * @author Linempy
 * @since 20.08.2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationEventPublisher implements EventPublisher<RecommendationEvent> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${redis.topic.recommendation}")
    private String recommendationTopic;

    public void publish(RecommendationEvent event) throws EventPublishingException {
        try {
            Long receiversCount = redisTemplate.convertAndSend(recommendationTopic, event);

            if (receiversCount != null && receiversCount > 0) {
                log.info("Событие успешно отправлено в топик {}. Получателей: {}",
                        recommendationTopic, receiversCount);
            } else {
                log.warn("Событие отправлено в топик {}, но нет активных подписчиков",
                        recommendationTopic);
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке события в топик {}: {}",
                    recommendationTopic, event, e);
            throw new EventPublishingException("Ошибка отправки ивента в Redis", e);
        }
    }

}