package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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
@RequiredArgsConstructor
public class RecommendationEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${redis.topic.recommendation}")
    private String recommendationTopic;

    public void publish(RecommendationEvent event) {
        String message = null;
        try {
            message = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.warn("Ивент рекомендации (id={}) не смог смаппиться в JSON", event.recommendationId());
            throw new RuntimeException(e);
        }

        redisTemplate.convertAndSend(recommendationTopic, message);
        log.info("Отправил ивент рекомендации");
    }

}