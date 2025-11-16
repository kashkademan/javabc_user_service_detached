package school.faang.user_service.config.redisson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RedissonClient;
import org.redisson.api.WorkerOptions;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import school.faang.user_service.config.redis.RedisProperties;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class RedissonConfig {
    private final RedisProperties redisProperties;
    @Value("${redisson.thread-pool-worker}")
    private int threadPoolWorker;
    @Value("${redisson.task-time-out-second}")
    private long taskTimeOutSecond;


    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://%s:%d".formatted(redisProperties.getHost(), redisProperties.getPort()))
                .setPassword(redisProperties.getPassword())
                .setDatabase(0);
        return Redisson.create(config);
    }

    @Bean(destroyMethod = "shutdown")
    public RScheduledExecutorService scheduledExecutorService(RedissonClient redissonClient) {
        String executorName = "eventNotificationsExecutor";

        RScheduledExecutorService executor = redissonClient.getExecutorService(executorName);

        executor.registerWorkers(WorkerOptions.defaults()
                .workers(threadPoolWorker)
                .taskTimeout(taskTimeOutSecond, TimeUnit.SECONDS));
        log.info("R2ScheduledExecutorService create bean {}", executorName);
        return executor;
    }
}