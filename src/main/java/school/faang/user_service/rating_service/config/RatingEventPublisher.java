package school.faang.user_service.rating_service.config;

import school.faang.user_service.rating_service.rating_aspect.UserActionEvent;

/**
 * Интерфейс для публикации событий пользовательских действий в систему рейтинга.
 * <p>
 * Предназначен для абстрагирования источника событий и их отправки в инфраструктуру,
 * такую как Kafka, RabbitMQ или любой другой механизм передачи сообщений.
 */
public interface RatingEventPublisher {
    void send(UserActionEvent event);
}