package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationReceivedEventDto;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationReceivedEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String NOTIFICATIONS_TOPIC = "recommendation-received-events";

    public void publish(long authorId,
                        long receiverId,
                        long recommendationId,
                        LocalDateTime createdAt) {
        RecommendationReceivedEventDto recommendationEvent = new RecommendationReceivedEventDto(recommendationId,
                authorId,
                receiverId,
                createdAt);
        log.info("Publishing recommendation event: recommendation Id = {}, author Id = {}, receiver Id = {}",
                recommendationId,
                authorId,
                receiverId);
        try {
            kafkaTemplate.send(NOTIFICATIONS_TOPIC, recommendationEvent);
            log.info("Successfully published recommendation event: {}", recommendationEvent);
        } catch (Exception e) {
            log.error("Failed to publish recommendation event: {}", recommendationEvent, e);
        }
    }
}
