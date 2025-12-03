package school.faang.user_service.messages.kafka.publusher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import school.faang.user_service.exception.KafkaSendException;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPublishKafka {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    public void publish(Object message) {
        try {
            log.info("Sending to the Kafka topic {}", message);
            String json;
            try {
                json = objectMapper.writeValueAsString(message);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            kafkaTemplate.send(new ProducerRecord<>(topic, json));
            log.info("Sending successful");
        } catch (KafkaSendException e) {
            log.info("Error to send Kafka topic {}", message);
        }
    }
}