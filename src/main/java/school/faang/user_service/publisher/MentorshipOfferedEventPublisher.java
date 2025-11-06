package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.mentorship.MentorshipOfferedEvent;

@Component
@RequiredArgsConstructor
public class MentorshipOfferedEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.mentorship-offer}")
    private String mentorshipOfferTopic;

    public void publish(MentorshipOfferedEvent mentorshipOfferedEvent) {
        try {
            String json = objectMapper.writeValueAsString(mentorshipOfferedEvent);
            kafkaTemplate.send(mentorshipOfferTopic, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}