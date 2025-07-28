package school.faang.user_service.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {
    @Value("${premium.scheduler.number-of-threads}")
    private int numberOfThreads;

    @Bean(destroyMethod = "shutdown")
    public ExecutorService fixedThreadPool() {
        return Executors.newFixedThreadPool(numberOfThreads);
    }
}
