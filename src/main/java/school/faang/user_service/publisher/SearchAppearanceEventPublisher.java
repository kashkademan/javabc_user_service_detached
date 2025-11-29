package school.faang.user_service.publisher;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.SearchAppearanceEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class SearchAppearanceEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.data.redis.topics.search-appearance}")
    private String searchAppearanceTopic;

    public void publish(SearchAppearanceEvent event) {
        try {
            redisTemplate.convertAndSend(searchAppearanceTopic, event);
            log.info("Event{} published:", event);
        } catch (RuntimeException e) {
            log.error("Error publishing event: {}", e.getMessage());
        }
    }
}