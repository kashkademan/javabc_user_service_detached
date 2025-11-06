package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventsPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String ANALYTICS_TOPIC = "analytics";

    public void sendEvent(String topic, EventDto event) {
        try {
            kafkaTemplate.send(topic, event);
            log.debug("Published event to topic {}: {}", topic, event);
        } catch (Exception e) {
            log.error("Failed to publish event to topic {}: {}", topic, event, e);
        }
    }

    public void publishProfileView(long profileId, long viewerId) {
        log.info("Publishing PROFILE_VIEW event: profileId={}, viewerId={}", profileId, viewerId);
        sendEvent(ANALYTICS_TOPIC, new EventDto(viewerId, profileId, "PROFILE_VIEW"));
    }

    public void publishFollow(long followerId, long followingId) {
        log.info("Publishing FOLLOWER event: followerId={}, followingId={}", followerId, followingId);
        sendEvent(ANALYTICS_TOPIC, new EventDto(followerId, followingId, "FOLLOWER"));
    }
}
