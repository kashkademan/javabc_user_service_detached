package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.KafkaTopicConfig;
import school.faang.user_service.dto.kafka.analytics.ProfileViewEvent;

@Component
public class ProfileViewEventPublisher extends AbstractEventPublisher<ProfileViewEvent> {

    public ProfileViewEventPublisher(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            KafkaTopicConfig kafkaTopicConfig) {
        super(kafkaTemplate, objectMapper, kafkaTopicConfig);
    }

    @Override
    protected String getTopicName() {
        return kafkaTopicConfig.getAnalytics().getProfileView();
    }

    @Override
    protected String getKey(ProfileViewEvent event) {
        return String.valueOf(event.userId());
    }
}

