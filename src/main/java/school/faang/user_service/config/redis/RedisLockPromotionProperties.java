package school.faang.user_service.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.data.redis.lock.promotion")
@Getter
@Setter
public class RedisLockPromotionProperties {
    private long expireTime;
    private int maxRetries;
    private int retryDelay;
}
