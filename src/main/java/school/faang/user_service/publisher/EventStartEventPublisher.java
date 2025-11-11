package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.KafkaTopicConfig;
import school.faang.user_service.dto.kafka.EventStartEventDto;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventStartEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicConfig kafkaTopicConfig;
    private Set<String> keyForKafka = new HashSet<>();

    public void publishEvent(EventStartEventDto eventStartEventDto, String key) {

        if (keyForKafka.contains(key)) {
            return;
        } else {
            keyForKafka.add(key);
        }

        String topic = kafkaTopicConfig.getEvents();
        kafkaTemplate.send(topic, key, eventStartEventDto).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully sent event to topic '{}', partition: {}, offset: {}",
                        topic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send event {} to topic '{}': {}",
                        eventStartEventDto,
                        topic,
                        ex.getMessage());
            }
        });
    }
}
