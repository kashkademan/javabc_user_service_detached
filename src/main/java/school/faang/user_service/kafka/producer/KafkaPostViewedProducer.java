package school.faang.user_service.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.kafka.event.ProfileViewedEvent;

@Component
@Slf4j
public class KafkaPostViewedProducer extends AbstractKafkaEventProducer<ProfileViewedEvent> {

    private static final String TOPIC = "profile.viewed";

    public KafkaPostViewedProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    @Override
    protected String getTopic() {
        return TOPIC;
    }
}
