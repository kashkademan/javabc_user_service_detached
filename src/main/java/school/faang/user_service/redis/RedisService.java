package school.faang.user_service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.UserService;

@Component
@RequiredArgsConstructor
public class RedisService implements MessageListener {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        Long id = Long.parseLong(message.toString());
        userService.userBan(id);
    }
}
