package school.faang.user_service.rating_service.entity;

/**
 * Интерфейс для обобщения event-сущностей
 *
 * @author Linempy
 * @since 03.09.2025
 */
public interface ScorableEvent {
    ActionType getActionType();

    Long getUserId();
}