package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.notification.NewFollowerEvent;

@Component
public class UnfollowEventPublisher extends AbstractEventPublisher<NewFollowerEvent> {

    public UnfollowEventPublisher(
            @Value(value = "${spring.kafka.topics.subscription.unfollow-topic.name}") String topic,
            KafkaTemplate<String, NewFollowerEvent> kafkaTemplate
    ) {
        super(topic, kafkaTemplate);
    }
}