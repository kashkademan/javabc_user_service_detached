package school.faang.user_service.messages.redis.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.GenericJacksonConfig;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class UsersBanListener implements MessageListener {
    private final UserService userService;
    private final GenericJacksonConfig genericJacksonConfig;

    @Override
    public void onMessage(@NotNull Message message, byte[] pattern) {
        log.info("Received message from channel: {}, body: {}", new String(pattern), new String(message.getBody()));
        Object value = genericJacksonConfig.getGenericJackson().deserialize(message.getBody());
        List<Long> usersIds = (List<Long>) value;
        userService.banUsers(usersIds);
    }
}