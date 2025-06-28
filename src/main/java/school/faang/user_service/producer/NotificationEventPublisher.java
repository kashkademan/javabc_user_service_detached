package school.faang.user_service.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.NotificationEvent;
import school.faang.user_service.entity.NotificationEventType;

@Service
@RequiredArgsConstructor
public class NotificationEventPublisher {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public void publishNotification(UserDto user, NotificationEventType type) {
        NotificationEvent event = new NotificationEvent(user, type);
        kafkaTemplate.send("notification.dispatch.v1", user.getId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.err.println("Failed to send message: " + ex.getMessage());
                    } else {
                        System.out.println("Message sent successfully, offset=" + result.getRecordMetadata().offset());
                    }
                });
    }
}