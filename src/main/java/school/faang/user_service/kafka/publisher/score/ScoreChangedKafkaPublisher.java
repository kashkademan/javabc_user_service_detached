package school.faang.user_service.kafka.publisher.score;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import school.faang.user_service.kafka.producer.score.UserScoreProducer;
import school.faang.user_service.model.score.UserScoreChangedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScoreChangedKafkaPublisher {

    private final UserScoreProducer kafkaProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onScoreChanged(UserScoreChangedEvent event) {
        kafkaProducer.sendScoreChanged(event);
    }
}

