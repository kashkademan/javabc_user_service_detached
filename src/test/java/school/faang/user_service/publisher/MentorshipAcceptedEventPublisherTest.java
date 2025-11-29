package school.faang.user_service.publisher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.event.mentorship.MentorshipAcceptedEvent;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipAcceptedEventPublisherTest {

    private final String mentorshipAcceptedTopic = "test1";

    private final MentorshipAcceptedEvent event = MentorshipAcceptedEvent.builder()
            .mentorId(23L)
            .mentorshipRequestId(1L)
            .build();

    @Captor
    private ArgumentCaptor<MentorshipAcceptedEvent> mentorshipAcceptedEventArgumentCaptor =
            ArgumentCaptor.forClass(MentorshipAcceptedEvent.class);

    @Mock
    private KafkaTemplate<String, MentorshipAcceptedEvent> kafkaTemplate;

    @InjectMocks
    private MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(mentorshipAcceptedEventPublisher,
                "mentorshipAcceptTopic", mentorshipAcceptedTopic);
    }

    @Test
    void testPublish() {
        CompletableFuture<SendResult<String, MentorshipAcceptedEvent>> future = CompletableFuture.completedFuture(null);

        when(kafkaTemplate.send(mentorshipAcceptedTopic, event.mentorId().toString(), event)).thenReturn(future);

        mentorshipAcceptedEventPublisher.publish(event);

        verify(kafkaTemplate).send(Mockito.eq(mentorshipAcceptedTopic), Mockito.eq(event.mentorId().toString()),
                mentorshipAcceptedEventArgumentCaptor.capture());

        MentorshipAcceptedEvent capturedEvent = mentorshipAcceptedEventArgumentCaptor.getValue();
        assertEquals(event.mentorshipRequestId(), capturedEvent.mentorshipRequestId());
    }
}