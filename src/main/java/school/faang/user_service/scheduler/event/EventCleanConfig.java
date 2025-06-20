package school.faang.user_service.scheduler.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "scheduler.past-event-clean")
@Component
public class EventCleanConfig {

    private String cron;
    private int batchSize;
    private int fetchLimit;
    private ExecutorConfig executorConfig = new ExecutorConfig();

    @Getter
    @Setter
    public static class ExecutorConfig {
        private int corePoolSize;
        private int maxPoolSize;
        private int queueCapacity;
        private String threadNamePrefix;
    }
}