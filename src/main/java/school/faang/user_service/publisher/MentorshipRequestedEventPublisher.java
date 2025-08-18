package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.system_event.MentorshipRequestedEvent;

@Component
public class MentorshipRequestedEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String channel;

    public MentorshipRequestedEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                             @Value("${spring.data.redis.channel.mentorship-requested}")
                                             String channel) {
        this.redisTemplate = redisTemplate;
        this.channel = channel;
    }

    public void publish(MentorshipRequestedEvent mentorshipRequestDto) {
        redisTemplate.convertAndSend(channel, mentorshipRequestDto);
    }
}