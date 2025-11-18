package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.event.NewFollowerEventDto;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewFollowerEventPublisherTest {

    private static final long FOLLOWER_ID = 1L;
    private static final long FOLLOWEE_ID = 2L;
    private static final String FOLLOWER_NAME = "Alice";

    private static final String DEFAULT_TOPIC = "follower-create-events-test";
    private static final String CUSTOM_TOPIC = "custom-topic";

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private SendResult<String, Object> sendResult;

    private NewFollowerEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new NewFollowerEventPublisher(kafkaTemplate);
        ReflectionTestUtils.setField(publisher, "userSubscriptionTopic", DEFAULT_TOPIC);
    }

    @Test
    @DisplayName("publishFollow: builds NewFollowerEventDto and sends to configured topic with receiverId as key")
    void publishFollow_sendsCorrectEvent() {

        when(kafkaTemplate.send(eq(DEFAULT_TOPIC), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        ArgumentCaptor<NewFollowerEventDto> eventCaptor =
                ArgumentCaptor.forClass(NewFollowerEventDto.class);

        publisher.publishEvent(FOLLOWER_ID, FOLLOWEE_ID, FOLLOWER_NAME);

        verify(kafkaTemplate).send(
                eq(DEFAULT_TOPIC),
                eq(Long.toString(FOLLOWEE_ID)),
                eventCaptor.capture()
        );
        verifyNoMoreInteractions(kafkaTemplate);

        NewFollowerEventDto sent = eventCaptor.getValue();
        assertEquals(FOLLOWER_ID, sent.actorId());
        assertEquals(FOLLOWEE_ID, sent.receiverId());
        assertEquals(FOLLOWER_NAME, sent.followerDisplayName());
    }

    @Test
    @DisplayName("sendEvent: uses provided topic and event.getKey() as Kafka key")
    void sendEvent_usesTopicAndKeyFromEvent() {
        when(kafkaTemplate.send(eq(CUSTOM_TOPIC), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        NewFollowerEventDto event =
                new NewFollowerEventDto(FOLLOWER_ID, FOLLOWEE_ID, FOLLOWER_NAME);

        publisher.sendEvent(CUSTOM_TOPIC, event);

        verify(kafkaTemplate).send(CUSTOM_TOPIC, event.getKey(), event);
        verifyNoMoreInteractions(kafkaTemplate);
    }

    @Test
    @DisplayName("sendEvent: does not throw if async send completes exceptionally")
    void sendEvent_doesNotThrowOnFailedFuture() {
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(eq(CUSTOM_TOPIC), anyString(), any()))
                .thenReturn(future);

        NewFollowerEventDto event =
                new NewFollowerEventDto(FOLLOWER_ID, FOLLOWEE_ID, FOLLOWER_NAME);

        assertDoesNotThrow(() -> publisher.sendEvent(CUSTOM_TOPIC, event));
        future.completeExceptionally(new RuntimeException("Kafka down"));

        verify(kafkaTemplate).send(CUSTOM_TOPIC, event.getKey(), event);
    }
}
