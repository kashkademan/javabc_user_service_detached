package school.faang.user_service.messages.redis.listeners;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.JsonParseException;
import school.faang.user_service.service.user.UserService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class UsersBanListener implements MessageListener {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(@NotNull Message message, byte[] pattern) {
        String channel = new String(pattern, StandardCharsets.UTF_8);
        log.info("Ban message received on channel: {}", channel);
        try {
            List<Long> userIds = objectMapper.readValue(
                    message.getBody(),
                    new TypeReference<List<Long>>() {}
            );
            userService.banUsers(userIds);
        } catch (IOException e) {
            throw new JsonParseException("Error to parse json" + e + getClass());
        }
    }
}