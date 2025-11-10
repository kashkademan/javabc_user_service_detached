package school.faang.user_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "topics")
public class KafkaTopicConfig {
    private TopicDefaults defaults;
    private NotificationsTopics notifications;
    private AnalyticsTopics analytics;
    private AchievementsTopics achievements;

    @Data
    public static class TopicDefaults {
        private Integer partitions;
        private Integer replicas;
    }

    @Data
    public static class NotificationsTopics {
        private String likeReceived;
    }

    @Data
    public static class AnalyticsTopics {
        private String profileView;
    }

    @Data
    public static class AchievementsTopics {
        private String goalCompleted;
    }
}


