package school.faang.user_service.messaging.publishers;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.messaging.events.GoalAttachedEvent;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class GoalAttachedMessagePublisher implements MessagePublisher<GoalAttachedEvent> {
    private static final String TOPIC_KEY = "goal_attached";

    private final RedisProperties properties;
    private String topic;
    @Autowired
    private CommonPublisher publisher;

    @PostConstruct
    private void init() {
        this.topic = properties.getChannels().get(TOPIC_KEY);
    }

    @Override
    public void publishMessage(GoalAttachedEvent event) {
        publisher.sendRedis(topic, event);
        publisher.sendKafka(topic, event);
    }

    public void createAndPublishMessage(Goal goal, Long userId) {
        GoalAttachedEvent event = new GoalAttachedEvent(userId,
                goal.getId(),
                goal.getTitle(),
                LocalDateTime.now());
        publishMessage(event);
    }
}
