package school.faang.user_service.messaging.publishers;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.messaging.events.GoalAttachedEvent;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class GoalAttachedMessagePublisher implements MessagePublisher<GoalAttachedEvent> {
    private static final String TOPIC_NAME = "goal_attached";

    private final RedisProperties properties;
    private String topic;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    private void init() {
        this.topic = properties.getChannels().get(TOPIC_NAME);
    }

    @Override
    public void publishMessage(GoalAttachedEvent event) {
        redisTemplate.convertAndSend(topic, event);
    }

    public void createAndPublishMessage(Goal goal, Long userId) {
        GoalAttachedEvent event = new GoalAttachedEvent(userId,
                goal.getId(),
                goal.getTitle(),
                LocalDateTime.now());
        publishMessage(event);
    }
}
