package school.faang.user_service.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.data.kafka.topic.event")
public class KafkaEventTopicProperties {
    private String name;
    private int partitions;
}
