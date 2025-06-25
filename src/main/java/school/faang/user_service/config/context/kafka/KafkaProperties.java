package school.faang.user_service.config.context.kafka;

import lombok.Data;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {

    private String bootstrapServers;
    private Producer producer = new Producer();

    @Data
    public static class Producer {
        private String acks = "all";
        private int retries = 5;
        private int deliveryTimeoutMs = 100000;
        private int lingerMs = 5;
        private int requestTimeoutMs = 3000;
        private int batchSize = 16384;
        private boolean enableIdempotence = true;
    }

    public Map<String, Object> getProducerProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.ACKS_CONFIG, producer.getAcks());
        properties.put(ProducerConfig.RETRIES_CONFIG, producer.getRetries());
        properties.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, producer.getDeliveryTimeoutMs());
        properties.put(ProducerConfig.LINGER_MS_CONFIG, producer.getLingerMs());
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, producer.getRequestTimeoutMs());
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, producer.getBatchSize());
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, producer.isEnableIdempotence());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return properties;
    }
}
