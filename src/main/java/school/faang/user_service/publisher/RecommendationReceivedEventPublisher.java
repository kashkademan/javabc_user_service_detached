package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationReceivedEventDto;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationReceivedEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.recommendation-received}")
    private String notificationTopic;

    public void publish(long authorId,
                        long receiverId,
                        long recommendationId,
                        LocalDateTime createdAt) {
        RecommendationReceivedEventDto recommendationEvent = new RecommendationReceivedEventDto(recommendationId,
                authorId,
                receiverId,
                createdAt);
        try {
            kafkaTemplate.send(notificationTopic, recommendationEvent);
            log.info("Successfully published recommendation event: {}", recommendationEvent);
        } catch (Exception e) {
            log.error("Failed to publish recommendation event: {}", recommendationEvent, e);
        }
    }
}
