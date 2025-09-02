package school.faang.user_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * SubProjectCreatedEventListener — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
 *
 * @author Linempy
 * @since 02.09.2025
 */
@Component
public class SubProjectCreatedEventListener extends AbstractMessageListener implements MessageListener {

    public SubProjectCreatedEventListener(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {

    }
}