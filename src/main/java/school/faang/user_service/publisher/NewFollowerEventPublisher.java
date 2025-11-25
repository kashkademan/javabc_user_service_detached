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

    public void sendEvent(NewFollowerEventDto event) {
        String key = String.valueOf(event.receiverId());

        CompletableFuture<SendResult<String, Object>> future
                = kafkaTemplate.send(userSubscriptionTopic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish event to topic {}: {}", userSubscriptionTopic, event, ex);
            } else {
                log.debug("Published event to topic {}: {}", userSubscriptionTopic, event);
            }
        });
    }

    public void publishEvent(long followerId, long followingId, String followerDisplayName) {
        log.info("Publishing new follower event: followerId={}, followingId={}, followerDisplayName={}",
                followerId, followingId, followerDisplayName);
        sendEvent(new NewFollowerEventDto(followerId, followingId, followerDisplayName));
    }
}
