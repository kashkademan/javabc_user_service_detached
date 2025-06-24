package school.faang.user_service.config;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.kafka.annotation.KafkaListener;
import school.faang.user_service.dto.notification.GoalCompletionNotificationEvent;

@TestComponent
public class EventListener {
    private GoalCompletionNotificationEvent receivedMessage;

    @KafkaListener(topics = "${spring.kafka.topics.test-topic.name}", groupId = "user-service-group")
    public void listen(GoalCompletionNotificationEvent event) {
        receivedMessage = event;
    }

    public GoalCompletionNotificationEvent getReceivedMessage() {
        return receivedMessage;
    }

    public void setReceivedMessage(GoalCompletionNotificationEvent receivedMessage) {
        this.receivedMessage = receivedMessage;
    }
}
