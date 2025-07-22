package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.FollowerEventDto;
import school.faang.user_service.publisher.FollowerEventPublisher;

import java.time.LocalDateTime;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FollowerEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic followerTopic;

    @InjectMocks
    private FollowerEventPublisher followerEventPublisher;

    @Test
    void testPublishSuccessful() {
        FollowerEventDto event = new FollowerEventDto(1L, 2L, LocalDateTime.now());
        String topicName = "follower_topic";

        when(followerTopic.getTopic()).thenReturn(topicName);

        followerEventPublisher.publish(event);

        verify(redisTemplate).convertAndSend(topicName, event);
    }

    @Test
    void testPublishException() {
        FollowerEventDto event = new FollowerEventDto(1L, 2L, LocalDateTime.now());
        String topicName = "follower_topic";

        when(followerTopic.getTopic()).thenReturn(topicName);
        doThrow(new RuntimeException("Test exception")).when(redisTemplate).convertAndSend(topicName, event);

        followerEventPublisher.publish(event);

        verify(redisTemplate).convertAndSend(topicName, event);
    }
}