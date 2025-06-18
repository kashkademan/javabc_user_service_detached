package school.faang.user_service.publisher.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.publisher.MessagePublisher;

@Slf4j
@Component("recommendationEventPublisher")
@RequiredArgsConstructor
public class RecommendationEventPublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    @Qualifier(value = "recommendationTopic")
    private final ChannelTopic topic;

    @Override
    public void publish(Object message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
        log.info("Recommendation event appeared. Recommendation event: {}", message);
    }
}
