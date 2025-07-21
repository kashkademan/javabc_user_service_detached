package school.faang.user_service.rating_service.rating_aspect;

import lombok.Getter;

/**
 * Перечисление (enum) типов действий пользователей,
 * за которые начисляются рейтинговые очки.
 * <p>
 * Используется для идентификации действия в системе рейтинга и
 * определения количества начисляемых баллов.
 *
 * @author agent
 * @since 19.07.2025
 */
@Getter
public enum ActionType {
    CREATE_POST(20),
    LIKE_POST(10),
    COMMENT_POST(30),
    ADD_EVENT(50),
    PARTICIPATION_IN_THE_EVENT(30),
    ADD_EDUCATION(25),
    ADD_CAREER(20),
    ADD_GOAL(50),
    BUY_PREMIUM(150),
    ADD_WORKSCHEDULE(50);

    private final int points;

    ActionType(int points) {
        this.points = points;
    }
}