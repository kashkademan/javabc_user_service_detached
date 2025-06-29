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

    @Bean(name = "addUserInRedisExecutor")
    public Executor addUserInRedisExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("RedisUserExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "decrementCountViewExecutor")
    public Executor decrementCountViewExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("DecrementCountViewExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "getEventExecutor")
    public Executor getEventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("DecrementCountViewExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "getUserExecutor")
    public Executor getUserExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("DecrementCountViewExecutor-");
        executor.initialize();
        return executor;
    }

    @Bean(name = "generateRandomAvatarUserExecutor")
    public Executor generateRandomAvatarUserExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}
