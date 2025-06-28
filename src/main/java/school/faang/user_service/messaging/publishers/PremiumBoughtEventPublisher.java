package school.faang.user_service.messaging.publishers;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.PremiumMapper;
import school.faang.user_service.messaging.events.PremiumBoughtEvent;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PremiumBoughtEventPublisher implements MessagePublisher<Premium> {
    private static final String TOPIC_NAME = "premium_bought";

    private final RedisProperties properties;
    private String topic;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PremiumMapper mapper;

    @PostConstruct
    private void init() {
        this.topic = properties.getChannels().get(TOPIC_NAME);
    }

    @Override
    public void publishMessage(Premium message) {
        log.debug("Publishing premium bought event by user with id {} - Started", message.getUser().getId());
        PremiumBoughtEvent event = mapper.toBoughtEvent(message);
        redisTemplate.convertAndSend(topic, event);
        log.debug("Publishing premium bought event by user with id {} - Finished", message.getUser().getId());
    }
}