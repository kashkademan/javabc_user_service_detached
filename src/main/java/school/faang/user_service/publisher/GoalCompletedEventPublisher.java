package school.faang.user_service.publisher;

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

    private final KafkaTemplate<String, GoalCompletionNotificationEvent> kafkaTemplate;

    @Value(value = "${spring.kafka.topics.goal-completed-topic.name}")
    private String goalCompletedTopicName;

    public void publish(GoalCompletionNotificationEvent event) {
            kafkaTemplate.send(goalCompletedTopicName, event);
    }
}
