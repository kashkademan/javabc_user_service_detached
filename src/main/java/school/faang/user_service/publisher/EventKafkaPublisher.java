package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.kafka.KafkaEventTopicProperties;
import school.faang.user_service.dto.event.EventResponseDto;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventKafkaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaEventTopicProperties commentProps;

    public void sendMessage(EventResponseDto eventDto) {
        kafkaTemplate.send(commentProps.getName(), eventDto).thenAccept(result ->
                log.info("Event with id={} sent to topic {}", eventDto.getId(), commentProps.getName()))
                .exceptionally(ex -> {
                    log.error("Failed to send event to Kafka topic '{}'. Error: {}",
                            commentProps.getName(), ex.getMessage());
                    return null;
                });
    }
}
