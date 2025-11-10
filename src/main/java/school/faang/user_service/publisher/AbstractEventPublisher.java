package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import school.faang.user_service.config.KafkaTopicConfig;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> {

    protected final KafkaTemplate<String, String> kafkaTemplate;
    protected final ObjectMapper objectMapper;
    protected final KafkaTopicConfig kafkaTopicConfig;

    public void publish(T event) {
        try {
            String topicName = getTopicName();
            String key = getKey(event);
            String eventJson = serializeEvent(event);

            kafkaTemplate.send(topicName, key, eventJson);

            logSuccess(topicName, key, event);

        } catch (JsonProcessingException e) {
            logError(event, e);
            throw new RuntimeException("Failed to publish event to Kafka", e);
        }
    }

    protected String serializeEvent(T event) throws JsonProcessingException {
        return objectMapper.writeValueAsString(event);
    }

    protected void logSuccess(String topicName, String key, T event) {
        log.info("Event published: topic='{}', key='{}', eventType='{}'",
                topicName, key, event.getClass().getSimpleName());
    }

    protected void logError(T event, Exception e) {
        log.error("Failed to serialize event: {}", event, e);
    }

    protected abstract String getTopicName();

    protected abstract String getKey(T event);
}

