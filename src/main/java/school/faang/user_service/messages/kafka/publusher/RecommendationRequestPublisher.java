package school.faang.user_service.messages.kafka.publusher;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class RecommendationRequestPublisher extends AbstractPublishKafka {
    public RecommendationRequestPublisher(@Qualifier("kafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate,
                                          @Value("${spring.kafka.topics.recommendation-request}") String topic) {
        super(kafkaTemplate, topic);
    }
}
