package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.mentorship.MentorshipOfferedEvent;
import school.faang.user_service.exception.EventPublishingException;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipOfferedEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic.mentorship-offer}")
    private String mentorshipOfferTopic;

    public void publish(MentorshipOfferedEvent mentorshipOfferedEvent) {
        try {

            String key = mentorshipOfferedEvent.mentorId().toString();
            String jsonBody = objectMapper.writeValueAsString(mentorshipOfferedEvent);
            CompletableFuture<SendResult<String, String>> future
                    = kafkaTemplate.send(mentorshipOfferTopic, key, jsonBody);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send mentorship offered event: Event {}, Mentor {} Mentee {}",
                            mentorshipOfferedEvent.mentorshipRequestId(), mentorshipOfferedEvent.mentorId(),
                            mentorshipOfferedEvent.menteeId());
                } else {
                    log.info("Sent mentorship offered event: Event {}, Mentor {} Mentee {}",
                            mentorshipOfferedEvent.mentorshipRequestId(), mentorshipOfferedEvent.mentorId(),
                            mentorshipOfferedEvent.menteeId());
                }
            });
        } catch (JsonProcessingException e) {
            String errorMessage = "JSON serialization failed for event: %d"
                    .formatted(mentorshipOfferedEvent.mentorshipRequestId());
            log.error(errorMessage);
            throw new EventPublishingException(errorMessage);
        }
    }
}