package school.faang.user_service.messages.kafka.publusher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import school.faang.user_service.exception.KafkaSendMessageException;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPublishKafka {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public void publish(Object message) {
        try {
            log.info("Sending to the Kafka topic {}", message);
            kafkaTemplate.send(topic, message);
            log.info("Sending successful");
        } catch (IllegalArgumentException e) {
            throw new KafkaSendMessageException("Error to send message to topic" + message + " " + topic);
        }
    }
}