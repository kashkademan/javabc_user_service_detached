package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.notification.GoalCompletionNotificationEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalCompletedEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value(value = "${spring.data.kafka.topics.goal-completed-topic.name}")
    private String goalCompletedTopicName;

    public void publish(GoalCompletionNotificationEvent event) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(goalCompletedTopicName, jsonEvent);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize to JSON. Event data: {}. Error message: {}",
                    event, e.getMessage(), e);
        }
    }
}
