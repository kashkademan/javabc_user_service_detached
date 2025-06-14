package school.faang.user_service.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {
    @Bean(name = "addEventInRedisExecutor")
    public Executor addEventInRedisExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("RedisEventExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "decrementCountViewExecutorExecutor")
    public Executor decrementCountViewExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("DecrementCountViewExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean
    public Executor generateRandomAvatarUserExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}
