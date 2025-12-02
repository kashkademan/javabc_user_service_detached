package school.faang.user_service.config.thread;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ScheduleThreadsPoolConfig {
    private static final String THREAD_NAME = "Task-Scheduler";
    @Value("${thread.taskScheduler.thread-pool-size}")
    private int poolSize;
    @Value("${thread.taskScheduler.await-termination-second}")
    private int awaitTerminationSecond;


    @Bean(name = "taskScheduler")
    public TaskScheduler getTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(poolSize);
        scheduler.setThreadNamePrefix(THREAD_NAME);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(awaitTerminationSecond);
        scheduler.initialize();
        return scheduler;
    }
}