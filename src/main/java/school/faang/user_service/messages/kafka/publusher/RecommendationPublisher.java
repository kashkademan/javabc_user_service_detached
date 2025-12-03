package school.faang.user_service.messages.kafka.publusher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class RecommendationPublisher extends AbstractPublishKafka{

    public RecommendationPublisher(@Qualifier("kafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate,
                                   @Value("${spring.kafka.topics.recommendation}") String eventRecommendation, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper, eventRecommendation);
    }
}