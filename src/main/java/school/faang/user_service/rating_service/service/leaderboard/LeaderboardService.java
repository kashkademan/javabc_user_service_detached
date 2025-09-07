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
    void processUserScore(ScorableEvent event, Double earnedScore);

}