package school.faang.user_service.scheduler.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@RequiredArgsConstructor
public class EventCleanExecutorConfig {

    private final EventCleanConfig config;

    @Bean(name = "pastEventCleanExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getExecutorConfig().getCorePoolSize());
        executor.setMaxPoolSize(config.getExecutorConfig().getMaxPoolSize());
        executor.setQueueCapacity(config.getExecutorConfig().getQueueCapacity());
        executor.setThreadNamePrefix(config.getExecutorConfig().getThreadNamePrefix());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}