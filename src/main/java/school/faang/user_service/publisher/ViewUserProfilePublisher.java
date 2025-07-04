package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.notification.ViewProfile;

@Component
public class ViewUserProfilePublisher extends AbstractEventPublisher<ViewProfile> {
    public ViewUserProfilePublisher(
            @Value(value = "${spring.kafka.topics.profile.name}") String topic,
            KafkaTemplate<String, ViewProfile> kafkaTemplate) {
        super(topic, kafkaTemplate);
    }
}
