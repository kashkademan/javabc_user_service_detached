package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.event.mentorship.MentorshipOfferedEvent;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipOfferedEventPublisherTest {

    private final String mentorshipOfferTopic = "test1";

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
        MentorshipOfferedEvent event = MentorshipOfferedEvent.builder()
                .mentorshipRequestId(1L)
                .build();

        when(objectMapper.writeValueAsString(event)).thenReturn(json);

        mentorshipOfferedEventPublisher.publish(event);

        verify(kafkaTemplate).send(Mockito.eq(mentorshipOfferTopic), Mockito.eq(json));
    }

    @Test
    void testPublish_ShouldThrowRuntimeExceptionWhenJsonProcessingFails() throws JsonProcessingException {
        MentorshipOfferedEvent event = MentorshipOfferedEvent.builder()
                .mentorshipRequestId(1L)
                .build();

        when(objectMapper.writeValueAsString(event)).thenThrow(JsonProcessingException.class);

        assertThrows(RuntimeException.class,
                () -> mentorshipOfferedEventPublisher.publish(event));

        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }
}