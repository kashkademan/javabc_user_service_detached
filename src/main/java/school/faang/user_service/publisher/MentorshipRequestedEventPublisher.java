package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.system_event.MentorshipRequestedEvent;

@Component
public class MentorshipRequestedEventPublisher {

    private final RedisTemplate<String, Object> mentorshipRedisTemplate;
    private final String channel;

    public MentorshipRequestedEventPublisher(RedisTemplate<String, Object> mentorshipRedisTemplate,
                                             @Value("${spring.data.redis.channel.mentorship-requested}")
                                             String channel) {
        this.mentorshipRedisTemplate = mentorshipRedisTemplate;
        this.channel = channel;
    }

    public void publish(MentorshipRequestedEvent mentorshipRequestDto) {
        mentorshipRedisTemplate.convertAndSend(channel, mentorshipRequestDto);
    }
}