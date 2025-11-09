package school.faang.user_service.kafka.producer;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.kafka.ProfileViewEvent;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileViewProducer {
    private final KafkaTemplate<String, Object> objectKafkaTemplate;

    public void sendToKafka(@NonNull ProfileViewEvent profileViewEvent) {
        String key = profileViewEvent.viewerId().toString() + profileViewEvent.profileOwnerId().toString();
        objectKafkaTemplate.send("profile-view", key, profileViewEvent);
        log.info("Новый ProfileViewEvent с viewerId: {} profileOwnerId: {} отправлен в Kafka.",
                profileViewEvent.viewerId(),
                profileViewEvent.profileOwnerId()
        );
    }
}
