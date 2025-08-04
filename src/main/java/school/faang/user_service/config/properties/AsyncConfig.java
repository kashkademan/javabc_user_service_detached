package school.faang.user_service.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {
    @Value("${premium.scheduler.number-of-threads}")
    private int numberOfThreads;

    @Bean("fixedThreadPool")
    public TaskExecutor fixedThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(numberOfThreads);
        executor.setMaxPoolSize(numberOfThreads);
        executor.setThreadNamePrefix("premium-removal-");
        executor.initialize();
        return executor;
    }
}
