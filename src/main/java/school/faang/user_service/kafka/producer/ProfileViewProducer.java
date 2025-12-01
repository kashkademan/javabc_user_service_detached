package school.faang.user_service.kafka.producer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.kafka.ProfileViewEvent;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileViewProducer {
    private final KafkaTemplate<String, Object> objectKafkaTemplate;

    @Value("${spring.data.kafka.topics.profile_view.name}")
    private String topicName;

    public void sendToKafka(@NonNull ProfileViewEvent profileViewEvent) {
        String key = generateKey(profileViewEvent);
        objectKafkaTemplate.send(topicName, key, profileViewEvent);
        log.info("Новый ProfileViewEvent с viewerId: {} profileOwnerId: {} отправлен в Kafka.",
                profileViewEvent.viewerId(),
                profileViewEvent.profileOwnerId()
        );
    }

    private String generateKey(ProfileViewEvent profileViewEvent) {
        return profileViewEvent.viewerId().toString() + profileViewEvent.profileOwnerId().toString();
    }
}
