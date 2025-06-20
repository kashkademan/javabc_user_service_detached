package school.faang.user_service.config.thread;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncThreadConfigPremiumRemove {

    @Bean(name = "asyncTaskExecutorPremiumRemove")
    public ThreadPoolTaskExecutor asyncTaskExecutorPremiumRemove() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(15);
        executor.setThreadNamePrefix("Premium-remove-async-thread");
        executor.initialize();

        return executor;
    }

}
