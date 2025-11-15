package school.faang.user_service.messaging.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.UserBanEvent;
import school.faang.user_service.service.user.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBanEventListener {

    private final UserService userService;


    @KafkaListener(topics = "${kafka.topic.user-ban}")
    public void onMessage(UserBanEvent userBanEvent) {

        if (!userBanEvent.userIds().isEmpty()) {
            userService.banUsers(userBanEvent.userIds());
        }
    }
}