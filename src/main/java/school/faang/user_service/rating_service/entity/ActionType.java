package school.faang.user_service.rating_service.entity;

import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.dto.post.PostPublishedEvent;
import school.faang.user_service.dto.project.SubProjectCreatedEvent;

import static school.faang.user_service.rating_service.entity.ActionLevel.EASY;

/**
 * Перечисление для определения действий, за которые начисляются баллы
 *
 * @author Linempy
 * @since 23.08.2025
 */
@Slf4j
public enum ActionType {
    POST_PUBLISHED(PostPublishedEvent.class, EASY),
    SUBPROJECT_CREATED(SubProjectCreatedEvent.class, EASY);

    private final Class<?> linkedClass;
    private final ActionLevel level;

    ActionType(Class<?> linkedClass, ActionLevel level) {
        this.linkedClass = linkedClass;
        this.level = level;
    }

    public ActionType getActionTypeByClass(Class<?> eventClass) {
        for (ActionType type : ActionType.values()) {
            if (type.linkedClass == eventClass) {
                return type;
            }
        }

        log.warn("Неизвестный event-класс{}", eventClass);
        throw new IllegalArgumentException("Неизвестный event-класс " + eventClass);
    }
}