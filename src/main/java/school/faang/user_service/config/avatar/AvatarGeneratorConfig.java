package school.faang.user_service.config.avatar;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AvatarGeneratorConfig {
    private static final int MAX_POOL_SIZE = 4;
    private static final int CORE_POOL_SIZE = 2;
    private static final int QUEUE_CAPACITY = 10;
    private static final String NAME_PREFIX = "avatar-generator-";

    @Bean(name = "avatar-generator-executor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setThreadNamePrefix(NAME_PREFIX);
        executor.initialize();
        return executor;
    }
}