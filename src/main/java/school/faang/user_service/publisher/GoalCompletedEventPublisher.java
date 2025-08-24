package school.faang.user_service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.events.GoalCompletedEvent;

@Component
@Slf4j
public class GoalCompletedEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String goalCompletedChannel;

    public GoalCompletedEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            @Value("${spring.data.redis.channel.goal_completed}") String goalCompletedChannel
    ) {
        this.redisTemplate = redisTemplate;
        this.goalCompletedChannel = goalCompletedChannel;
    }

    public void publish(GoalCompletedEvent goalCompletedEvent) {
        try {
            redisTemplate.convertAndSend(goalCompletedChannel, goalCompletedEvent);
            log.info("Published GoalCompletedEvent: userId={}, goalId={}",
                    goalCompletedEvent.userId(), goalCompletedEvent.goalId());
        } catch (Exception e) {
            log.error("Failed to publish GoalCompletedEvent: userId={}, goalId={}",
                    goalCompletedEvent.userId(), goalCompletedEvent.goalId(), e);
            throw new RuntimeException("Failed to publish goal completion event", e);
        }
    }
}
