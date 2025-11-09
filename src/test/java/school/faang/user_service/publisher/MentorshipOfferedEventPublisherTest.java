package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.event.mentorship.MentorshipOfferedEvent;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipOfferedEventPublisherTest {

    private final String mentorshipOfferTopic = "test1";

    private final MentorshipOfferedEvent event = MentorshipOfferedEvent.builder()
            .mentorId(23L)
            .mentorshipRequestId(1L)
            .build();

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private MentorshipOfferedEventPublisher mentorshipOfferedEventPublisher;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(mentorshipOfferedEventPublisher,
                "mentorshipOfferTopic", mentorshipOfferTopic);
    }

    @Test
    void testPublish() throws JsonProcessingException {
        String json = "json";
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(null);

        when(kafkaTemplate.send(mentorshipOfferTopic, event.mentorId().toString(), json)).thenReturn(future);
        when(objectMapper.writeValueAsString(event)).thenReturn(json);

        mentorshipOfferedEventPublisher.publish(event);

        verify(kafkaTemplate).send(mentorshipOfferTopic, event.mentorId().toString(), json);
    }

    @Test
    void testPublish_ShouldThrowRuntimeExceptionWhenJsonProcessingFails() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(event)).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class, () -> mentorshipOfferedEventPublisher.publish(event));

        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }


    @Test
    void testPublish_WhenKafkaSendFails() throws JsonProcessingException {
        String json = "json";
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka error"));

        when(kafkaTemplate.send(mentorshipOfferTopic, event.mentorId().toString(), json)).thenReturn(future);
        when(objectMapper.writeValueAsString(event)).thenReturn(json);

        mentorshipOfferedEventPublisher.publish(event);

        verify(kafkaTemplate).send(mentorshipOfferTopic, event.mentorId().toString(), json);
    }
}