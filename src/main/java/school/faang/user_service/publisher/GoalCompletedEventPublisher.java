package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.notification.GoalCompletionNotificationEvent;

@Component
public class GoalCompletedEventPublisher extends AbstractEventPublisher<GoalCompletionNotificationEvent> {

    public GoalCompletedEventPublisher(
            @Value(value = "${spring.kafka.topics.goal-completed-topic.name}") String topic,
            KafkaTemplate<String, GoalCompletionNotificationEvent> kafkaTemplate
    ) {
        super(topic, kafkaTemplate);
    }
}
