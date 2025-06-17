package school.faang.user_service.messaging.publishers;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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
    private static final String TOPIC_NAME = "goal_complete";

    private final RedisProperties properties;
    private String topic;
    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    private void init() {
        this.topic = properties.getChannels().get(TOPIC_NAME);
    }

    @Override
    public void publishMessage(Goal goal) {
        List<Long> userIds = goal.getUsers().stream().map(User::getId).toList();

        GoalCompletedEvent event = new GoalCompletedEvent(goal.getId(), goal.getTitle(), userIds, LocalDateTime.now());
        redisTemplate.convertAndSend(topic, event);
    }
}
