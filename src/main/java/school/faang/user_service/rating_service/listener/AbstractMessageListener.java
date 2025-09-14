package school.faang.user_service.rating_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Общая логика слушателя ивентов
 *
 * @author Linempy
 * @since 23.08.2025
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractMessageListener<E>  implements MessageListener {


    protected final ObjectMapper objectMapper;

    protected void handleMessage(Message message, Class<E> type, Consumer<E> handler) {
        try {
            E event = objectMapper.readValue(message.getBody(), type);
            handler.accept(event);
        } catch (IOException e) {
            log.error("Ошибка в десереализации");
            throw new RuntimeException(e);
        }
    }


}