package school.faang.user_service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.JsonDeserializationException;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class AbstractEventListener {

    private final ObjectMapper objectMapper;

    public <T> void receiveAndHandle(String message, Class<T> eventType, Consumer<T> handler, Acknowledgment ack) {
        try {
            log.debug("Received new event: {}", message);
            T event = objectMapper.readValue(message, eventType);
            ack.acknowledge();
            handler.accept(event);
        } catch (JsonProcessingException e) {
            throw new JsonDeserializationException("Deserialization json %s to event object error", message);
        } catch (Exception e) {
            log.error("Error handling event: {}", message, e);
        }
    }
}
