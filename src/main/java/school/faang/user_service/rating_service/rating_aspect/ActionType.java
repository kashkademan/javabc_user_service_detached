package school.faang.user_service.rating_service.rating_aspect;

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
public enum ActionType {
    CREATE_POST,
    LIKE_POST,
    COMMENT_POST,
    ADD_EVENT,
    PATRICIPATION_IN_THE_EVENT,
    ADD_EDUCATION,
    ADD_CAREER,
    ADD_GOAL,
    BUY_PREMIUM,
    ADD_WORKSCHEDULE
}