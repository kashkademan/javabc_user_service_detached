package school.faang.user_service.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka.topics")
public class KafkaTopicsProperties {
    private String userScoreChanged;

    public String getDltTopicFor(String topicName) {
        return topicName + ".DLT";
    }
}
