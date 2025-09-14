package school.faang.user_service.rating_service.service.leaderboard;

import school.faang.user_service.rating_service.dto.user.UserScoreViewDto;
import school.faang.user_service.rating_service.entity.ScorableEvent;

import java.util.List;

/**
 * Сервис для сохранения действий пользователя, получения топа пользователей
 *
 * @author Linempy
 * @since 29.08.2025
 */
public interface LeaderboardService {

    /**
     * Метод для заполнения баллов пользователя в Redis и в Postgres
     *
     * @param event действие, за которое пользователь получает баллы
     * @param earnedScore кол-во заработанных баллов
     */
    void processUpdateUserScore(ScorableEvent event, Double earnedScore);

    /**
     * Метод для получения топа пользователей
     *
     * @param size кол-во получаемых пользователей
     * @param page номер страницы
     * @return список {@link UserScoreViewDto}
     */
    List<UserScoreViewDto> getTopScores(Integer size, Integer page);

    /**
     * Метод для получения кол-во баллов конкретного пользователя
     *
     * @param userId ID пользователя
     * @return {@link UserScoreViewDto}
     */
    UserScoreViewDto getUserScore(Long userId);

}