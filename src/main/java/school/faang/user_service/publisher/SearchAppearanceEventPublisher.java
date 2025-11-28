package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.SearchAppearanceEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class SearchAppearanceEventPublisher implements MessagePublisher<SearchAppearanceEvent> {

    @Value("${spring.data.redis.channel.search-appearance}")
    private final ChannelTopic topic;
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(SearchAppearanceEvent event) {
        try {
            redisTemplate.convertAndSend(topic.getTopic(), event);
            log.info("Event{} published:", event);
        } catch (RuntimeException e) {
            log.error("Error publishing event: {}", e.getMessage());
        }
    }
}