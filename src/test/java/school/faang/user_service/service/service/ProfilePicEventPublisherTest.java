package school.faang.user_service.service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.event.ProfilePicEvent;
import school.faang.user_service.publisher.ProfilePicEventPublisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfilePicEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic profilePicTopic;

    @InjectMocks
    private ProfilePicEventPublisher profilePicEventPublisher;

    @Test
    void testPublishShouldSendEventToRedis() {
        ProfilePicEvent event = ProfilePicEvent.builder()
                .userId(1L)
                .newFileId("new_file.jpg")
                .oldFileId("old_file.jpg")
                .changedAt(java.time.LocalDateTime.now())
                .build();

        when(profilePicTopic.getTopic()).thenReturn("profile-pic-topic");

        profilePicEventPublisher.publish(event);

        verify(redisTemplate).convertAndSend("profile-pic-topic", event);
        verify(profilePicTopic).getTopic();
    }

    @Test
    void testPublishWithNullEventShouldThrowException() {
        assertThrows(NullPointerException.class,
                () -> profilePicEventPublisher.publish(null));
    }

    @Test
    void testPublishShouldUseCorrectTopic() {
        ProfilePicEvent event = mock(ProfilePicEvent.class);
        when(profilePicTopic.getTopic()).thenReturn("custom-topic");

        profilePicEventPublisher.publish(event);

        verify(redisTemplate).convertAndSend("custom-topic", event);
        verify(profilePicTopic, times(1)).getTopic();
    }
}