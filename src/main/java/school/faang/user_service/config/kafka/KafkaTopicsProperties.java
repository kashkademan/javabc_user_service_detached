package school.faang.user_service.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "topics")
@Getter
@Setter
public class KafkaTopicsProperties {
    private String userScoreChanged;
    private String userScoreChangedDlt;
}

