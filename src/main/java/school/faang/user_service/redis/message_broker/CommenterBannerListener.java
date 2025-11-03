package school.faang.user_service.redis.message_broker;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommenterBannerListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final UserService userService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            Long userIdForBan = objectMapper.readValue(message.getBody(), Long.class);
            userService.banUser(userIdForBan);
        } catch (IOException e) {
            log.info("При попытки принятия сообщения CommenterBannerListener произошла ошибка.");
            throw new RuntimeException(e);
        }
    }
}
