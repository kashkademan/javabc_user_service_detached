package school.faang.user_service.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.SubscriptionService;

@Component
@RequiredArgsConstructor
public class AuthorRequestEventListener {

    private final AbstractEventListener abstractEventListener;
    private final SubscriptionService subscriptionService;

    @KafkaListener(
            topics = "${spring.data.kafka.topic.author-request.name}",
            groupId = "${spring.data.kafka.consumer.group-id}"
    )
    public void receiveAndHandle(String message, Acknowledgment ack) {
        //TODO: сменить метод на получение подписчиков, а также тип на PostPublishEvent
        abstractEventListener.receiveAndHandle(message, Long.class, subscriptionService::getFollowerIds, ack);
    }
}
