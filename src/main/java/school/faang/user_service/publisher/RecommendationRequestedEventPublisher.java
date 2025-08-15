package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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
@RequiredArgsConstructor
public class RecommendationRequestedEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${redis.topic.recommendation-request}")
    private String recommendationRequestTopic;

    public void publish(RecommendationRequestedEvent event) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("Ивент отправлен в топик {}", recommendationRequestTopic);
        redisTemplate.convertAndSend(recommendationRequestTopic, json);
    }
}