package school.faang.user_service.service.publisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.SkillAcquiredEvent;
import school.faang.user_service.publisher.SkillAcquiredEventPublisher;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SkillAcquiredEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic skillAcquiredTopic;

    @InjectMocks
    private SkillAcquiredEventPublisher publisher;

    @Test
    public void testPublishSkillAcquired() {
        org.mockito.Mockito.when(skillAcquiredTopic.getTopic()).thenReturn("skill_acquired_topic");

        publisher.publishSkillAcquired(1L, 2L);

        verify(redisTemplate, times(1)).convertAndSend(
                eq("skill_acquired_topic"),
                argThat(event -> {
                    SkillAcquiredEvent e = (SkillAcquiredEvent) event;
                    return e.getUserId().equals(1L) && e.getSkillId().equals(2L);
                })
        );
    }
}
