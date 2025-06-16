package school.faang.user_service.messaging.publishers;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.messaging.events.GoalCompletedEvent;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Setter
public class GoalCompletedMessagePublisher {
    private final String topicName = "goal_complete";

    private final RedisMessagePublisher messagePublisher;
    private final RedisProperties properties;
    private String topic;

    @PostConstruct
    private void init() {
        this.topic = properties.getChannels().get(topicName);
    }

    public void publishMessage(Goal goal) {
        GoalCompletedEvent event = new GoalCompletedEvent(goal.getId(), goal.getTitle(), LocalDateTime.now());
        messagePublisher.publishMessage(topic, event);
    }
}
