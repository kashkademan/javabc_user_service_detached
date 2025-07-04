package school.faang.user_service.publisher.profile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.ProfileViewEventDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProfileViewEventPublisher implements ProfileViewEventPublisher {

    private final KafkaTemplate<String, ProfileViewEventDto> kafkaTemplate;

    @Value("${spring.kafka.topics.profile-view}")
    private String topic;

    @Override
    public void publish(ProfileViewEventDto event) {
        kafkaTemplate.send(topic, event.getProfileOwnerId().toString(), event)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("Failed to send ProfileViewEvent: {}", event, exception);
                    }
                });
    }
}
