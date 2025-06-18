package school.faang.user_service.service.publisher;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.event.MentorshipRequestEvent;
import school.faang.user_service.publisher.MentorshipRequestEventPublisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MentorshipRequestEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private MentorshipRequestEventPublisher publisher;

    @Test
    public void methodWorks() {
        MentorshipRequestEvent event = new MentorshipRequestEvent(1L, 1L, 1);
        publisher.publish(event);
        verify(redisTemplate, times(1)).convertAndSend("mentorshipRequest_topic", event);
        verify(publisher, times(1)).publish(any());
    }
}
