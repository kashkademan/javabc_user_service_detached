package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.mentorship.MentorshipOfferedEvent;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipOfferedEventPublisher {

    private final KafkaTemplate<String, MentorshipOfferedEvent> kafkaTemplate;

    @Value("${kafka.topic.mentorship-offer}")
    private String mentorshipOfferTopic;

    public void publish(MentorshipOfferedEvent mentorshipOfferedEvent) {
        String key = mentorshipOfferedEvent.mentorId().toString();

        CompletableFuture<SendResult<String, MentorshipOfferedEvent>> future
                = kafkaTemplate.send(mentorshipOfferTopic, key, mentorshipOfferedEvent);

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
    }
}