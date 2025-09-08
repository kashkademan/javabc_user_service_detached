package school.faang.user_service.rating_service.dto;

import school.faang.user_service.rating_service.entity.ActionType;

/**
 * ScorableEvent — описание интерфейса.
 * <p>
 * TODO: описать, какие обязанности реализует интерфейс.
 * </p>
 *
 * @author Linempy
 * @since 03.09.2025
 */
public interface ScorableEvent {
    ActionType getActionType();

    Long getUserId();
}