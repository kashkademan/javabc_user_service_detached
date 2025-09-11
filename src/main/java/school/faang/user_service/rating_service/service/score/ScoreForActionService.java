package school.faang.user_service.rating_service.service.score;

import school.faang.user_service.rating_service.entity.ScorableEvent;

/**
 * Сервис для получения кол-ва баллов по event-классу
 *
 * @author Linempy
 * @since 05.09.2025
 */
public interface ScoreForActionService {
    Double getScore(ScorableEvent event);
}