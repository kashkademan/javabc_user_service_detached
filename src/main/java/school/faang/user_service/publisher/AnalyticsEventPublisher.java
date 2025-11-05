package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(EventDto eventDto) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(eventDto);
            redisTemplate.convertAndSend("analytics", jsonEvent);
            log.debug("Publisher analytics event: {}", eventDto);
        } catch (Exception e) {
            log.error("Publisher analytics event failed: {}", eventDto, e);
        }
    }

    public void publishProfileView(long profileId, long viewerId) {
        log.info("Publishing PROFILE_VIEW event: profileId={}, viewerId={}", profileId, viewerId);
        publish(new EventDto(viewerId, profileId, "PROFILE_VIEW"));
    }

    public void publishFollow(long followerId, long followingId) {
        log.info("Publishing FOLLOWER event: followerId={}, followingId={}", followerId, followingId);
        publish(new EventDto(followerId, followingId, "FOLLOWER"));
    }
}
