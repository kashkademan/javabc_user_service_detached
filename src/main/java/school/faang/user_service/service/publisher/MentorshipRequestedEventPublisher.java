package school.faang.user_service.service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.service.event.MentorshipRequestedEvent;

@Service
@RequiredArgsConstructor
public class MentorshipRequestedEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.mentorship_request}")
    private String channelName;

    public void publish(MentorshipRequestedEvent event) {
        redisTemplate.convertAndSend(channelName, event);
    }
}
