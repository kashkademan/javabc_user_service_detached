package school.faang.user_service.messaging.producer.redis_pub_sub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.messaging.dto.SearchAppearanceEvent;
import school.faang.user_service.messaging.producer.EventPublisher;

import java.util.Objects;

/**
 * SearchAppearanceEventPublisher — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>
 *
 * @author Myrza
 * @since 19.08.2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SearchAppearanceEventPublisher implements EventPublisher<SearchAppearanceEvent> {
    private final RedisTemplate<String, SearchAppearanceEvent> redisTemplate;

    @Value("${kafka.topics.search-appearance}")
    private String searchAppearanceTopic;

    @Override
    public void publish(SearchAppearanceEvent event) {
        if (Objects.equals(event.visitorId(), event.visitedId())) {
            log.info("visit yourself");
            return;
        }

        log.info("publish to '{}' topic, event: {}", searchAppearanceTopic, event);
        redisTemplate.convertAndSend(searchAppearanceTopic, event);
    }
}
