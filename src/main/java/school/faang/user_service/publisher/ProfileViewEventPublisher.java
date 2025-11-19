package school.faang.user_service.publisher;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.KafkaTopicConfig;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.kafka.analytics.ProfileViewEvent;

import java.time.Instant;

@Component
public class ProfileViewEventPublisher extends AbstractEventPublisher<ProfileViewEvent> {
    private final UserContext userContext;

    public ProfileViewEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            KafkaTopicConfig kafkaTopicConfig,
            UserContext userContext) {
        super(kafkaTemplate, kafkaTopicConfig);
        this.userContext = userContext;
    }

    @Override
    protected String getTopicName() {
        return kafkaTopicConfig.getAnalytics().getProfileView();
    }

    @Override
    protected String getKey(ProfileViewEvent event) {
        return userContext.getUser().getId() + "_" + Instant.now().toEpochMilli();
    }
}

