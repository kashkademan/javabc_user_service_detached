package school.faang.user_service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventConsumer {

    @KafkaListener(topics = "${topics.notifications}", groupId = "notification-group")
    public void handleNotification(String message) {
        log.info("Received notification: {}", message);
    }

    @KafkaListener(topics = "${topics.analytics}", groupId = "notification-group")
    public void handleAnalytics(String message) {
        log.info("Received analytics: {}", message);
    }

    @KafkaListener(topics = "${topics.achievements}", groupId = "notification-group")
    public void handleAchievement(String message) {
        log.info("Received achievement: {}", message);
    }
}
