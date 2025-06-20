package school.faang.user_service.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class KafkaServer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    public void sendMessage(String message, long key) {
        kafkaTemplate.send("view-topic", message, key);
        log.info("Message sent");
    }
}
