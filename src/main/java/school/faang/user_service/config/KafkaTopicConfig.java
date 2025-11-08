package school.faang.user_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "topics")
public class KafkaTopicConfig {
    private String notifications;
    private String analytics;
    private String achievements;
}


