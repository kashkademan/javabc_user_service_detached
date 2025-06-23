package school.faang.user_service.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka.topic.event")
public class KafkaEventTopicProperties {
    private String name;
    private int partitions;
}
