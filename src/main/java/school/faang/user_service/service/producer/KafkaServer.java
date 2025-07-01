package school.faang.user_service.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.kafka.ProfileViewDto;
import school.faang.user_service.dto.user.UserDto;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Service
public class KafkaServer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${kafka.template.topic}")
    private String topic;

    public void sendProfileViewNotification(UserDto profileUser, UserDto viewerUser) {
        ProfileViewDto notification = new ProfileViewDto(
                profileUser,
                viewerUser,
                LocalDateTime.now()
        );
        kafkaTemplate.send(topic, notification);
    }
}
