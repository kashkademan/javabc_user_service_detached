package school.faang.user_service.messaging.publishers;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.messaging.events.FollowerEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowerEventPublisher implements MessagePublisher<FollowerEvent>{
    private static final String REDIS_TOPIC_KEY = "follower_event";

    private final RedisProperties properties;
    private String redisTopic;
    @Autowired
    private final CommonPublisher publisher;

    @PostConstruct
    private void init() {
        this.redisTopic = properties.getChannels().get(REDIS_TOPIC_KEY);
    }

    @Override
    public void publishMessage(FollowerEvent followerEvent) {
        publisher.sendRedis(redisTopic, followerEvent);
    }
}