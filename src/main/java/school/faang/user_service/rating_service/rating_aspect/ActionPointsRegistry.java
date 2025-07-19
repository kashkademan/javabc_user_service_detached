package school.faang.user_service.rating_service.rating_aspect;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Регистр соответствия типов действий пользователя и начисляемых за них баллов.
 * <p>
 * Служит для определения, сколько очков должно быть начислено за каждое действие,
 * зафиксированное в системе.
 * <p>
 * Используется компонентами рейтингового сервиса для подсчёта рейтинга пользователя.
 */
@Component
public class ActionPointsRegistry {
    private static final Map<ActionType, Integer> POUNTS = Map.of(
            ActionType.COMMENT_POST, 30,
            ActionType.ADD_EVENT, 50,
            ActionType.LIKE_POST, 10,
            ActionType.CREATE_POST, 20,
            ActionType.PATRICIPATION_IN_THE_EVENT, 30,
            ActionType.ADD_EDUCATION, 25,
            ActionType.ADD_CAREER, 20,
            ActionType.ADD_GOAL, 50,
            ActionType.BUY_PREMIUM, 150,
            ActionType.ADD_WORKSCHEDULE, 50
    );

    public int getPoints(ActionType actionType) {
        return POUNTS.getOrDefault(actionType, 0);
    }
}