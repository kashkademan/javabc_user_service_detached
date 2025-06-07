package school.faang.user_service.subscriber;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBanSubscriber implements MessageListener {
    private final UserService userService;

    @Override
    public void onMessage(@NotNull Message message, @Nullable byte[] pattern) {
        log.info("Message received. Banned userBan id: {}", message);
        try {
            long userId = Long.parseLong(message.toString());
            userService.banUser(userId);
        } catch (Exception e) {
            log.error("Error on processing userBan {} ban: ", message, e);
        }

        log.info("User {} ban completed.", message);
    }
}
