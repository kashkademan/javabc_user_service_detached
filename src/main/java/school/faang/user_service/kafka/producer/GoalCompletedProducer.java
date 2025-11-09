package school.faang.user_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.kafka.GoalCompletedEvent;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalCompletedProducer {
    private final KafkaTemplate<String, Object> objectKafkaTemplate;

    public void sendGoalCompletedEventToKafka(GoalCompletedEvent event) {
        String key = event.goalId().toString() + event.userId().toString();
        objectKafkaTemplate.send("goal-completed", key, event);
        log.info("Новый CompletedGoalEvent с ключом: {} отправлен в Kafka.", event);
    }
}
