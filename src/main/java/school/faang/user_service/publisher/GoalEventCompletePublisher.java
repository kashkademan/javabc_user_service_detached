package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalCompleteEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalEventCompletePublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channel.goal}")
    private String goalTopic;

    @Async
    @Retryable(retryFor = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void publish(GoalCompleteEvent goal) {
        log.info("Publishing goal event: {}", goal);
        redisTemplate.convertAndSend(goalTopic, goal);
    }
}
