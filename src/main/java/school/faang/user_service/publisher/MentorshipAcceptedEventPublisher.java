package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.mentorship.MentorshipAcceptedEvent;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipAcceptedEventPublisher {

    private final KafkaTemplate<String, MentorshipAcceptedEvent> kafkaTemplate;

    @Value("${kafka.topic.mentorship-accept}")
    private String mentorshipAcceptTopic;

    public void publish(MentorshipAcceptedEvent mentorshipAcceptedEvent) {
        String key = mentorshipAcceptedEvent.mentorId().toString();

        CompletableFuture<SendResult<String, MentorshipAcceptedEvent>> future
                = kafkaTemplate.send(mentorshipAcceptTopic, key, mentorshipAcceptedEvent);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send mentorship accept event: Event {}, Mentor {} Mentee {}",
                        mentorshipAcceptedEvent.mentorshipRequestId(), mentorshipAcceptedEvent.mentorId(),
                        mentorshipAcceptedEvent.menteeId());
            } else {
                log.info("Sent mentorship accept event: Event {}, Mentor {} Mentee {}",
                        mentorshipAcceptedEvent.mentorshipRequestId(), mentorshipAcceptedEvent.mentorId(),
                        mentorshipAcceptedEvent.menteeId());
            }
        });
    }
}