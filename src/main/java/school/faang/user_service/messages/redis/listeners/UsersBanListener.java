package school.faang.user_service.messages.redis.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class UsersBanListener implements MessageListener {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public void onMessage(@NotNull Message message, byte[] pattern) {
        String channel = new String(pattern);
        String body = new String(message.getBody());

        log.info("Received message from channel: {}, body: {}", channel, body);
        List<Long> usersIdList = deserializeUserIds(body);
        if (!usersIdList.isEmpty()) {
            userRepository.bannedByIds(usersIdList);
            log.debug("Successfully banned {} users", usersIdList.size());
        } else {
            log.debug("Empty lists for banning users!");
        }
    }

    private List<Long> deserializeUserIds(String messageBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(messageBody,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Long.class));
        } catch (Exception e) {
            log.error("Error deserializing user IDs: {}", e.getMessage());
            return List.of();
        }
    }
}