package school.faang.user_service.config.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import school.faang.user_service.model.score.UserScoreChangedEvent;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaTopicsProperties.class)
@Slf4j
public class KafkaListenerConfig {

    private final KafkaProperties kafkaProperties;
    private final KafkaTopicsProperties topicsProperties;
    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Value("${spring.kafka.retry.backoff}")
    private long retryBackoff;

    @Value("${spring.kafka.retry.attempts}")
    private long retryAttempts;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserScoreChangedEvent> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, UserScoreChangedEvent>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties()));
        factory.setCommonErrorHandler(kafkaErrorHandler());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler() {
        var recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> {
                    String dltTopic = topicsProperties.getDltTopicFor(record.topic());
                    log.error("Сообщение отправлено в DLT: topic={}, key={}, причина={}",
                            record.topic(), record.key(), ex.getMessage());
                    return new TopicPartition(dltTopic, record.partition());
                });

        var errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(retryBackoff, retryAttempts));
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.warn("Попытка #{} не удалась: topic={}, key={}, причина={}",
                    deliveryAttempt, record.topic(), record.key(), ex.getMessage());
        });

        return errorHandler;
    }
}
