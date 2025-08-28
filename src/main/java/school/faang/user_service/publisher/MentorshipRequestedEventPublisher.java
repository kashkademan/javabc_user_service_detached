package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisConfigProperties;
import school.faang.user_service.dto.system_event.MentorshipRequestedEvent;

@Component
@RequiredArgsConstructor
public class MentorshipRequestedEventPublisher {

    private final RedisTemplate<String, Object> mentorshipRedisTemplate;
    private final RedisConfigProperties redisConfigProperties;

    public void publish(MentorshipRequestedEvent mentorshipRequestDto) {
        mentorshipRedisTemplate.convertAndSend(redisConfigProperties.getMentorshipRequestChannel(),
                mentorshipRequestDto);
    }
}