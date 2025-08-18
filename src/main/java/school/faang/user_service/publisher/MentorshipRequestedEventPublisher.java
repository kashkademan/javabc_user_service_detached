package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.MentorshipRequestedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestedEventPublisher {

    private static final String CHANNEL = "mentorship.requested";

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(MentorshipRequestedEvent event) {
        log.info("Отправляю событие запроса на менторство: {}", event);
        redisTemplate.convertAndSend(CHANNEL, event);

    }
}