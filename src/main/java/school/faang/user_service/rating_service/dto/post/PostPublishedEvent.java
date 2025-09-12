package school.faang.user_service.rating_service.dto.post;

import school.faang.user_service.rating_service.entity.ScorableEvent;
import school.faang.user_service.rating_service.entity.ActionType;

/**
 * Класс-ивент для уведомления о публикации поста
 *
 * @author Linempy
 * @since 23.08.2025
 */
public record PostPublishedEvent(
        Long postId,
        Long authorId,
        Long projectId
) implements ScorableEvent {

    @Override
    public ActionType getActionType() {
        return ActionType.POST_PUBLISHED;
    }

    @Override
    public Long getUserId() {
        return authorId;
    }
}