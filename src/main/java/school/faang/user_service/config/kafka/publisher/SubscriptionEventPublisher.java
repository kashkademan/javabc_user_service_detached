package school.faang.user_service.config.kafka.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.kafka.dto.SubscriptionEvent;

@Component
public class SubscriptionEventPublisher extends AbstractEventPublisher<SubscriptionEvent> {

    public SubscriptionEventPublisher(
            @Value(value = "${spring.data.kafka.topic.subscription}") String topic,
            KafkaTemplate<String, SubscriptionEvent> kafkaTemplate
    ) {
        super(topic, kafkaTemplate);
    }
}