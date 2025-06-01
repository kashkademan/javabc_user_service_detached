package school.faang.user_service.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import school.faang.user_service.component.UserSubscriptionsProcessor;
import school.faang.user_service.dto.event.FeedWarmupBatchEvent;

@Component
@RequiredArgsConstructor
public class FeedWarmupEventListener {

    private final AbstractEventListener eventListener;
    private final UserSubscriptionsProcessor userSubscriptionsProcessor;

    @KafkaListener(
            topics = "${spring.data.kafka.topic.feed-warmup.name}",
            groupId = "${spring.data.kafka.consumer.group-id}"
    )
    public void receiveAndHandle(String message, Acknowledgment ack) {
        eventListener.receiveAndHandle(message, FeedWarmupBatchEvent.class,
                userSubscriptionsProcessor::processUserSubscriptions, ack);
    }
}
