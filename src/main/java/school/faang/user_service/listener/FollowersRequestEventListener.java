package school.faang.user_service.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.PostPublishEvent;
import school.faang.user_service.service.SubscriptionService;

@Component
@RequiredArgsConstructor
public class FollowersRequestEventListener {

    private final AbstractEventListener abstractEventListener;
    private final SubscriptionService subscriptionService;

    @KafkaListener(
            topics = "${spring.data.kafka.topic.followers-request.name}",
            groupId = "${spring.data.kafka.consumer.group-id}"
    )
    public void receiveAndHandle(String message, Acknowledgment ack) {
        abstractEventListener.receiveAndHandle(message, PostPublishEvent.class,
                subscriptionService::findFollowerIdsForPostAuthor, ack);
    }
}
