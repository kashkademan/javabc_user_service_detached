package school.faang.user_service.messages.redis.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
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
        log.info("Received message from channel: {}, body: {}", new String(pattern), new String(message.getBody()));
        Object value = new GenericJackson2JsonRedisSerializer().deserialize(message.getBody());
        List<Long> usersIdList = (List<Long>) value;
        if (!usersIdList.isEmpty()) {
            userRepository.bannedByIds(usersIdList);
            log.debug("Successfully banned {} users", usersIdList.size());
        } else {
            log.debug("Empty lists for banning users!");
        }
    }
}