package school.faang.user_service.config.thread;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncThreadConfigPremiumRemove {

    @Bean(name = "asyncTaskExecutorPremiumRemove")
    @ConfigurationProperties(prefix = "app.thread.pool.premium-remove")
    public ThreadPoolTaskExecutor asyncTaskExecutorPremiumRemove() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.initialize();

        return executor;
    }
}