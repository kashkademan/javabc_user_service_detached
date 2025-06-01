package school.faang.user_service.config.context;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {
    private String host;
    private int port;
    private int timeout = 2000;
    private Pool pool = new Pool();

    @Getter
    @Setter
    public static class Pool {
        private int maxTotal = 10;
        private int maxIdle = 10;
        private int minIdle = 10;
    }
}
