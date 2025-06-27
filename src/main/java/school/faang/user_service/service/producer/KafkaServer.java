package school.faang.user_service.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class KafkaServer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${kafka.template.topic}")
    private String topic;

    public void sendProfileViewNotification(Long profileId, Long viewerId) {
        String message = String.format("{\"viewedUserId\": %d, \"viewerUserId\": %d}", profileId, viewerId);
        kafkaTemplate.send(topic, message);
    }
}
