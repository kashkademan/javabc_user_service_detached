package school.faang.user_service.messaging.publishers;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.messaging.events.FollowerEvent;
import java.time.LocalDateTime;

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
    public void createAndPublishMessage(long followerId, long followeeId) {
        log.debug("""
                Create and publishing Follower Event with follower id: {},\
                and foloweeId: {} was started
                """, followerId, followeeId);
        FollowerEvent followerEvent = FollowerEvent.builder()
                .followerId(followerId)
                .followeeId(followeeId)
                .subscriptionTime(LocalDateTime.now())
                .build();

        publishMessage(followerEvent);
        log.debug("""
                Publishing Follower Event with follower id: {}
                and with followee id: {} was finished
                """, followerId, followeeId);
    }
}