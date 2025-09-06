package school.faang.user_service.rating_service.service.leaderboard;

import school.faang.user_service.dto.ScorableEvent;

/**
 * RatingService — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
 *
 * @author Linempy
 * @since 29.08.2025
 */
public interface LeaderboardService {
    boolean isExistsUserInCache(Long userId);

    boolean isExistsUserInMemory(Long userId);

    void incrementUserScoreAsync(ScorableEvent event, int increment);

    void createUserScoreAsync(ScorableEvent event, int score);

    Double getScoreByUserId(Long userId);

    void getTopUsers(int count);

    void updateUser(ScorableEvent event, int increment);

    void saveUser(ScorableEvent event, int score);

}