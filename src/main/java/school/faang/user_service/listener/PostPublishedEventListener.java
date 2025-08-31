package school.faang.user_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.post.PostPublishedEvent;

/**
 * Класс-слушатель (подписчик) ивентов публикации поста
 *
 * @author Linempy
 * @since 23.08.2025
 */
@Slf4j
@Component
public class PostPublishedEventListener extends AbstractMessageListener implements MessageListener {

    public PostPublishedEventListener(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        PostPublishedEvent event = objectMapper.readValue(message.getBody(), PostPublishedEvent.class);

    }
}