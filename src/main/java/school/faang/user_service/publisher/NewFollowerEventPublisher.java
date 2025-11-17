package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.NewFollowerEventDto;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewFollowerEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.topic.follower-create-events:follower-create-events}")
    private String userSubscriptionTopic;

    public void sendEvent(String topic, NewFollowerEventDto event) {
        String key = event.getKey();

        CompletableFuture<SendResult<String, Object>> future
                = kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish event to topic {}: {}", topic, event, ex);
            } else {
                log.debug("Published event to topic {}: {}", topic, event);
            }
        });
    }

    public void publishFollow(long followerId, long followingId, String followerDisplayName) {
        log.info("Publishing FOLLOWER event: followerId={}, followingId={}, followerDisplayName={}",
                followerId, followingId, followerDisplayName);
        sendEvent(userSubscriptionTopic, new NewFollowerEventDto(followerId, followingId, followerDisplayName));
    }
}
