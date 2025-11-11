package school.faang.user_service.messaging.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.UserBanEvent;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBanEventListener {
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @PostConstruct
    public void init() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @KafkaListener(topics = "${kafka.topic.user-ban}")
    public void onMessage(String event) {
        UserBanEvent userBanEvent = deserializeEvent(event);

        if (!userBanEvent.userIds().isEmpty()) {
            userService.banUsers(userBanEvent.userIds());
        }
    }

    private UserBanEvent deserializeEvent(String event) {
        UserBanEvent userBanEvent = null;
        try {
            userBanEvent = objectMapper.readValue(event, UserBanEvent.class);
        } catch (JsonProcessingException e) {
            String errorMessage = "Failed to deserialize event %s".formatted(event);
            log.error(errorMessage);
            throw new DataValidationException(errorMessage);
        }
        return userBanEvent;
    }
}