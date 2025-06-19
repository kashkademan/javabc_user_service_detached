package school.faang.user_service.service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.event.MentorshipRequestEvent;
import school.faang.user_service.publisher.MentorshipRequestEventPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    MentorshipRequestEventPublisher publisher;

    @Test
    public void methodWorks() {
        MentorshipRequestEvent event = new MentorshipRequestEvent(1L, 1L, 1);
        publisher.publish(event);
        verify(redisTemplate, times(1)).convertAndSend(eq("mentorshipRequest_topic"), any());
    }
}
