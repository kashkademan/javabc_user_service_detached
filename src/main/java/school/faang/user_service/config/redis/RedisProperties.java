package school.faang.user_service.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("spring.data.redis")
public record RedisProperties(
//        String host,
//        int port,
        @NestedConfigurationProperty Channels channels
) {
    public record Channels(
            String userBan,
            String recommendationEvent
    ) {
    }
}
