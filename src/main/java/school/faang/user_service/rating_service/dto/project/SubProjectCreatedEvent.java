package school.faang.user_service.rating_service.dto.project;

import school.faang.user_service.rating_service.entity.ActionType;
import school.faang.user_service.rating_service.entity.ScorableEvent;

/**
 * Класс-ивент для уведомления о создании подпроекта
 *
 * @author Linempy
 * @since 23.08.2025
 */
public record SubProjectCreatedEvent(
        Long parentId,
        Long id,
        Long ownerId
) implements ScorableEvent {

    @Override
    public ActionType getActionType() {
        return ActionType.SUBPROJECT_CREATED;
    }

    @Override
    public Long getUserId() {
        return ownerId;
    }
}