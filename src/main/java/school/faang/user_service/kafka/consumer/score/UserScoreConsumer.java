package school.faang.user_service.kafka.consumer.score;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.processor.score.UserScoreProcessor;
import school.faang.user_service.model.score.UserScoreChangedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserScoreConsumer {

    private final UserScoreProcessor processor;

    @KafkaListener(
            topics = "${topics.user-score-changed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(UserScoreChangedEvent changeScoreEvent, Acknowledgment ack) {
        try {
            processor.process(changeScoreEvent);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Ошибка при обработке события: {}", changeScoreEvent, e);
            throw e;
        }
    }
}
