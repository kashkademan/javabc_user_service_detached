package school.faang.user_service.rating_service.dto;

import school.faang.user_service.rating_service.entity.UserScore;

/**
 * Проекция для сущности {@link UserScore}
 *
 * @author Linempy
 * @since 08.09.2025
 */
public interface UserScoreProjection {
    Long getUserId();

    Double getScore();
}