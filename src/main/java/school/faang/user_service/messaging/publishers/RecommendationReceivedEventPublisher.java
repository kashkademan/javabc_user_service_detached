package school.faang.user_service.messaging.publishers;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.messaging.events.RecommendationReceivedEvent;


@Slf4j
@Component("recommendationEventPublisher")
@RequiredArgsConstructor
public class RecommendationReceivedEventPublisher implements MessagePublisher<Recommendation> {
    private static final String TOPIC_NAME = "recommendation_event";

    private final RedisProperties properties;
    private String topic;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RecommendationMapper mapper;

    @PostConstruct
    private void init() {
        this.topic = properties.getChannels().get(TOPIC_NAME);
    }

    @Override
    public void publishMessage(Recommendation message) {
        RecommendationReceivedEvent event = mapper.toEvent(message);
        redisTemplate.convertAndSend(topic, event);
        log.info("Recommendation received event was published: {}", event);
    }
}
