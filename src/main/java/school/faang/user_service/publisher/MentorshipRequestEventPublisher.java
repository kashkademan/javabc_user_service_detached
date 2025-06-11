package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.MentorshipRequestEvent;

@Component
@RequiredArgsConstructor
public class MentorshipRequestEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void publish(MentorshipRequestEvent event) {
        redisTemplate.convertAndSend("mentorshipRequest_topic", event);
    }
}
