package school.faang.user_service.config;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.kafka.annotation.KafkaListener;

@TestComponent
public class EventListener {
    private String receivedMessage;

    @KafkaListener(topics = "${spring.kafka.topics.test-topic.name}", groupId = "user-service-group")
    public void listen(String event) {
        receivedMessage = event;
    }

    public String getReceivedMessage() {
        return receivedMessage;
    }

    public void setReceivedMessage(String receivedMessage) {
        this.receivedMessage = receivedMessage;
    }
}
