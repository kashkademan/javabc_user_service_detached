package school.faang.user_service.rating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.listener.PostPublishedEventListener;
import school.faang.user_service.listener.SubProjectCreatedEventListener;

import java.util.Map;

/**
 * Класс для создания ассоциации {@code КЛАСС_ИВЕНТА: ACTION_TYPE}
 *
 * @author Linempy
 * @since 02.09.2025
 */

@Slf4j
@Component
public class AssociationEventToAction {

    private final Map<Class<?>, ActionType> basicActions;
    private final Map<Class<?>, ActionType> complexActions;

    public AssociationEventToAction() {
        this.basicActions = Map.of(
                PostPublishedEventListener.class, ActionType.POST_PUBLISHED,
                SubProjectCreatedEventListener.class, ActionType.SUBPROJECT_CREATED
        );
        this.complexActions = Map.of(
                PostPublishedEventListener.class, ActionType.POST_PUBLISHED,
                SubProjectCreatedEventListener.class, ActionType.SUBPROJECT_CREATED
        );
    }

    public ActionType getBasicActionType(Class<?> eventClass) {
        return getActionType(eventClass, basicActions, "базового");
    }

    public ActionType getComplexActionType(Class<?> eventClass) {
        return getActionType(eventClass, complexActions, "сложного");
    }

    private ActionType getActionType(Class<?> eventClass, Map<Class<?>, ActionType> actions, String actionType) {
        ActionType eventType = actions.get(eventClass);
        if (eventType == null) {
            log.warn("Неизвестный event-класс для {} действия: {}", actionType, eventClass);
            throw new IllegalArgumentException("Неизвестный event-класс: " + eventClass);
        }
        return eventType;
    }

    public boolean hasBasicAction(Class<?> eventClass) {
        return basicActions.containsKey(eventClass);
    }

    public boolean hasComplexAction(Class<?> eventClass) {
        return complexActions.containsKey(eventClass);
    }

    public Map<Class<?>, ActionType> getAllBasicActions() {
        return Map.copyOf(basicActions);
    }

    public Map<Class<?>, ActionType> getAllComplexActions() {
        return Map.copyOf(complexActions);
    }
}