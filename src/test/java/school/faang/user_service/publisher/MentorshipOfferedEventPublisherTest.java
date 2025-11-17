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
import school.faang.user_service.event.mentorship.MentorshipOfferedEvent;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipOfferedEventPublisherTest {

    private final String mentorshipOfferTopic = "test1";

    private final MentorshipOfferedEvent event = MentorshipOfferedEvent.builder()
            .mentorId(23L)
            .mentorshipRequestId(1L)
            .build();

    @Captor
    private ArgumentCaptor<MentorshipOfferedEvent> mentorshipOfferedEventArgumentCaptor =
            ArgumentCaptor.forClass(MentorshipOfferedEvent.class);

    @Mock
    private KafkaTemplate<String, MentorshipOfferedEvent> kafkaTemplate;

    @InjectMocks
    private MentorshipOfferedEventPublisher mentorshipOfferedEventPublisher;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(mentorshipOfferedEventPublisher,
                "mentorshipOfferTopic", mentorshipOfferTopic);
    }

    @Test
    void testPublish() {
        CompletableFuture<SendResult<String, MentorshipOfferedEvent>> future = CompletableFuture.completedFuture(null);

        when(kafkaTemplate.send(mentorshipOfferTopic, event.mentorId().toString(), event)).thenReturn(future);

        mentorshipOfferedEventPublisher.publish(event);

        verify(kafkaTemplate).send(Mockito.eq(mentorshipOfferTopic), Mockito.eq(event.mentorId().toString()),
                mentorshipOfferedEventArgumentCaptor.capture());

        MentorshipOfferedEvent capturedEvent = mentorshipOfferedEventArgumentCaptor.getValue();
        assertEquals(event.mentorshipRequestId(), capturedEvent.mentorshipRequestId());
    }
}