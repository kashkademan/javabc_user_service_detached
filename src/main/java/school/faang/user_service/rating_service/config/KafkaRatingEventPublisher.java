package school.faang.user_service.rating_service.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import school.faang.user_service.rating_service.rating_aspect.UserActionEvent;

/**
 * Компонент для публикации событий пользовательской активности в Kafka.
 * <p>
 * Используется для асинхронной отправки {@link UserActionEvent} в указанный Kafka-топик.
 * Ключом сообщения является идентификатор пользователя в виде строки.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaRatingEventPublisher implements RatingEventPublisher {

    /**
     * Kafka-шаблон для сериализации и отправки сообщений с ключом типа {@code String}
     * и значением типа {@link UserActionEvent}.
     */
    private final KafkaTemplate<String, UserActionEvent> kafkaTemplate;

    /**
     * Название Kafka-топика, в который отправляются события.
     * Значение читается из application.yml/properties.
     */
    @Value("${kafka.topics.user-actions}")
    private String topic;

    /**
     * Отправляет событие {@link UserActionEvent} в Kafka-топик.
     *
     * @param event объект события, содержащий информацию о действии пользователя
     */
    @Override
    public void send(UserActionEvent event) {
        kafkaTemplate.send(topic, event.getUserId().toString(), event);
        log.debug("send event: {}", event);
    }
}