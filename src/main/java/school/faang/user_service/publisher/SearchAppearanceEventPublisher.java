package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.SearchAppearanceEvent;

@Component
public class SearchAppearanceEventPublisher extends AbstractEventPublisher<SearchAppearanceEvent> {
    public SearchAppearanceEventPublisher(@Value("{spring.data.redis.channel.search-appearance}") String channelName,
                                          RedisTemplate<String, Object> redisTemplate) {
        super(channelName, redisTemplate);
    }

}
