package school.faang.user_service.config.execservice;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class AsyncExecutor {

    private final static long MAX_WAIT_MILLIS = 300000;

    @Bean({"expiredEventTaskExecutor"})
    public ThreadPoolTaskExecutor getAsyncExecutor(int threadCount) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadCount);
        executor.setMaxPoolSize(threadCount);
        executor.setThreadNamePrefix("expiredEventTaskExecutor-");
        executor.setAwaitTerminationMillis(MAX_WAIT_MILLIS);
        executor.initialize();
        return executor;
    }
}