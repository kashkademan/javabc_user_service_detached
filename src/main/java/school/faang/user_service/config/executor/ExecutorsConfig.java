package school.faang.user_service.config.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class ExecutorsConfig {

    private final ExecutorsProperties properties;

    @Bean(name = "addEventInRedisExecutor")
    public Executor addEventInRedisExecutor() {
        return buildExecutor(properties.getAddEventInRedis());
    }

    @Bean(name = "addUserInRedisExecutor")
    public Executor addUserInRedisExecutor() {
        return buildExecutor(properties.getAddUserInRedis());
    }

    @Bean(name = "getUserInRedisExecutor")
    public Executor getUserInRedisExecutor() {
        return buildExecutor(properties.getGetUserInRedis());
    }

    @Bean(name = "getEventInRedisExecutor")
    public Executor getEventInRedisExecutor() { return buildExecutor(properties.getGetEventInRedis()); }

    @Bean(name = "decrementCountViewExecutor")
    public Executor decrementCountViewExecutor() { return buildExecutor(properties.getDecrementCountView()); }

    @Bean(name = "kafkaMessageExecutor")
    public Executor kafkaMessageExecutor() {
        return buildExecutor(properties.getKafkaMessage());
    }

    @Bean(name = "generateRandomAvatarUserExecutor")
    public Executor generateRandomAvatarUserExecutor() {
        return new SimpleAsyncTaskExecutor("GenerateAvatarUser-");
    }

    private ThreadPoolTaskExecutor buildExecutor(ExecutorsProperties.ExecutorProps config) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getCorePoolSize());
        executor.setMaxPoolSize(config.getMaxPoolSize());
        executor.setQueueCapacity(config.getQueueCapacity());
        executor.setThreadNamePrefix(config.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }
}
