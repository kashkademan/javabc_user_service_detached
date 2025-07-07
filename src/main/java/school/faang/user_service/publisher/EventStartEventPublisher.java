package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.notification.EventStartNotificationEvent;

@Component
public class EventStartEventPublisher extends AbstractEventPublisher<EventStartNotificationEvent>{

    public EventStartEventPublisher(
            @Value(value = "${spring.kafka.topics.event-start-topic.name}") String topic,
            KafkaTemplate<String, EventStartNotificationEvent> kafkaTemplate) {
        super(topic, kafkaTemplate);
    }
}
