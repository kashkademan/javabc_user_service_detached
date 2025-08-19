package school.faang.user_service.messaging.consumer.redispubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import school.faang.user_service.mapper.analytics.ProfileVisitMapper;
import school.faang.user_service.messaging.dto.SearchAppearanceEvent;
import school.faang.user_service.service.analytics.ProfileVisitService;

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
@RequiredArgsConstructor
public class SearchAppearanceEventConsumer implements MessageListener {
    private final ObjectMapper objMapper;
    private final ProfileVisitService service;
    private final ProfileVisitMapper visitMapper;

    @Override
    public void onMessage(@NotNull Message message, byte[] pattern) {
        var topic = new String(pattern, StandardCharsets.UTF_8);
        try {
            var event = objMapper.readValue(message.getBody(), SearchAppearanceEvent.class);
            log.info("start consume message on topic: '{}' event: {}", topic, event);
            var dto = visitMapper.toDto(event);
            service.addVisit(dto);
        } catch (Exception e) {
            log.error("SearchAppearanceEventConsumer.onMessage topic: {}", topic, e);
        }
    }
}
