package school.faang.user_service.rating_service.service.score;

import school.faang.user_service.dto.ScorableEvent;

/**
 * ScoreService — описание интерфейса.
 * <p>
 * TODO: описать, какие обязанности реализует интерфейс.
 * </p>
 *
 * @author Linempy
 * @since 05.09.2025
 */
public interface ScoreService {
    Double getScore(ScorableEvent event);
}