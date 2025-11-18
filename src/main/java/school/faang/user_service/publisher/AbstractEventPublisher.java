package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import school.faang.user_service.config.KafkaTopicConfig;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> {

    protected final KafkaTemplate<String, Object> kafkaTemplate;
    protected final KafkaTopicConfig kafkaTopicConfig;

    public void publish(T event) {
        String topicName = getTopicName();
        String key = getKey(event);

        kafkaTemplate.send(topicName, key, event);

        logSuccess(topicName, key, event);
    }

    protected void logSuccess(String topicName, String key, T event) {
        log.info("Event published: topic='{}', key='{}', eventType='{}'",
                topicName, key, event.getClass().getSimpleName());
    }

    protected abstract String getTopicName();

    protected abstract String getKey(T event);
}

