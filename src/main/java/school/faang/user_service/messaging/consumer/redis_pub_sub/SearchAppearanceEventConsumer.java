package school.faang.user_service.messaging.consumer.redis_pub_sub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import school.faang.user_service.messaging.dto.SearchAppearanceEvent;

import java.nio.charset.StandardCharsets;

/**
 * SearchAppearanceEventListener — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>
 *
 * @author Myrza
 * @since 19.08.2025
 */
@Service
@Slf4j
public class SearchAppearanceEventConsumer implements MessageListener {
    private final ObjectMapper objMapper;

    public SearchAppearanceEventConsumer() {
        objMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Override
    public void onMessage(@NotNull Message message, byte[] pattern) {
        var topic = new String(pattern, StandardCharsets.UTF_8);
        log.info("start consume message on topic: {}", topic);
        try {
            var body = objMapper.readValue(message.getBody(), SearchAppearanceEvent.class);
            log.info("body {}", body);
        } catch (Exception e) {
            log.error("SearchAppearanceEventConsumer.onMessage topic: {}", topic, e);
        }
    }
}
