package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.config.KafkaTopicConfig;

@RequiredArgsConstructor
@RequestMapping("/debug")
@RestController
public class DebugController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTopicConfig topicConfig;

    @GetMapping("/kafka/send-notification")
    public String sendNotification() {
        String topic = topicConfig.getNotifications();
        String message = """
            {"userId": 123, "text": "Welcome!", "timestamp": "%d"}
            """.formatted(System.currentTimeMillis());

        kafkaTemplate.send(topic, message);
        return "Sent to " + topic;
    }

    @GetMapping("/kafka/send-analytics")
    public String sendAnalytics() {
        String topic = topicConfig.getAnalytics();
        String message = """
            {"event": "page_view", "url": "/home", "timestamp": "%d"}
            """.formatted(System.currentTimeMillis());

        kafkaTemplate.send(topic, message);
        return "Sent to " + topic;
    }

    @GetMapping("/kafka/send-achievement")
    public String sendAchievement() {
        String topic = topicConfig.getAchievements();
        String message = """
            {"userId": 123, "achievement": "first_login", "timestamp": "%d"}
            """.formatted(System.currentTimeMillis());

        kafkaTemplate.send(topic, message);
        return "Sent to " + topic;
    }
}
