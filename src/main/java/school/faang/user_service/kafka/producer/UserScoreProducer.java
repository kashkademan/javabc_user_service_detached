package school.faang.user_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.kafka.KafkaTopicsProperties;
import school.faang.user_service.model.score.UserScoreChangedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserScoreProducer {

    private final KafkaTemplate<String, UserScoreChangedEvent> kafkaTemplate;
    private final KafkaTopicsProperties topics;;

    public void sendScoreChanged(UserScoreChangedEvent changeScoreEvent) {
        String topic = topics.getUserScoreChanged();
        kafkaTemplate.send(topic, String.valueOf(changeScoreEvent.getUserId()), changeScoreEvent);
        log.info("Message {} sended to topic {}", changeScoreEvent, topic);
    }
}
