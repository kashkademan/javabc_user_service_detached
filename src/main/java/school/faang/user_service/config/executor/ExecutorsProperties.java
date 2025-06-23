package school.faang.user_service.config.executor;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "executors")
@Data
public class ExecutorsProperties {

    private ExecutorProps addEventInRedis;
    private ExecutorProps decrementCountView;
    private ExecutorProps kafkaMessage;

    @Data
    public static class ExecutorProps {
        private int corePoolSize;
        private int maxPoolSize;
        private int queueCapacity;
        private String threadNamePrefix;
    }
}
