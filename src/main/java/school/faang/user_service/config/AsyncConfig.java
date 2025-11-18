package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {

    @Bean(destroyMethod = "shutdown")
    public ExecutorService executorService(@Value("${scheduler.thread-pool-size}") int threadPoolSize) {
        System.out.println("Spring создаёт ExecutorService");
        return Executors.newFixedThreadPool(threadPoolSize);
    }
}