package school.faang.user_service.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {
    // TODO
    @Bean(name = "addEventInRedisExecutor")
    public Executor addEventInRedisExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4); // начальное количество потоков
        executor.setMaxPoolSize(10); // максимум потоков
        executor.setQueueCapacity(100); // очередь задач
        executor.setThreadNamePrefix("RedisEventExecutor-");
        executor.initialize();
        return executor;
    }
}
