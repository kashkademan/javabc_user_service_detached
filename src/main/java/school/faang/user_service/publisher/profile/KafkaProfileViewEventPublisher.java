package school.faang.user_service.publisher.profile;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.ProfileViewEventDto;

@Component
@RequiredArgsConstructor
public class KafkaProfileViewEventPublisher implements ProfileViewEventPublisher {

    private final KafkaTemplate<String, ProfileViewEventDto> kafkaTemplate;

    @Value("${spring.kafka.topics.profile-view}")
    private String topic;

    public void publish(ProfileViewEventDto event) {
        kafkaTemplate.send(topic, event.getProfileOwnerId().toString(), event);
    }
}
