package school.faang.user_service.messaging.publishers;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.messaging.events.GoalCompletedEvent;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalCompletedMessagePublisher implements MessagePublisher<Goal> {
    private static final String REDIS_TOPIC_KEY = "goal_complete";
    public static final String KAFKA_TOPIC = "goal_completed";

    private final RedisProperties properties;
    private String redisTopic;
    @Autowired
    private CommonPublisher publisher;

    @PostConstruct
    private void init() {
        this.redisTopic = properties.getChannels().get(REDIS_TOPIC_KEY);
    }

    @Override
    public void publishMessage(Goal goal) {
        List<Long> userIds = goal.getUsers().stream().map(User::getId).toList();
        GoalCompletedEvent event = new GoalCompletedEvent(goal.getId(), goal.getTitle(), userIds, LocalDateTime.now());
        publisher.sendRedis(redisTopic, event);
        publisher.sendKafka(KAFKA_TOPIC, event);
    }
}
