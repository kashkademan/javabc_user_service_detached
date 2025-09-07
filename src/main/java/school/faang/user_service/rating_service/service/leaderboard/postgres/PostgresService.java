package school.faang.user_service.rating_service.service.leaderboard.postgres;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.ScorableEvent;
import school.faang.user_service.rating_service.repository.UserActionLogRepository;
import school.faang.user_service.rating_service.repository.UserScoreRepository;

/**
 * PostgresService — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
 *
 * @author Linempy
 * @since 05.09.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostgresService {
    private final UserActionLogRepository logRepository;
    private final UserScoreRepository scoreRepository;

    @Transactional
    @Async("postgresTaskExecutor")
    public void upsertUserScore(ScorableEvent event, Double score) {
        saveUserActionLog(event, score);

        scoreRepository.upsertScore(event.getUserId(), score);

        log.info("Баллы пользователя id={} были обновлены", event.getUserId());
    }

    public Double getUserScore(Long userId) {
        return scoreRepository.findScoreByUserId(userId);
    }

    private void saveUserActionLog(ScorableEvent event, Double score) {
        logRepository.save(event.getUserId(), event.getActionType().name(), score);
        log.info("Действие пользователя id={} было сохранено", event.getUserId());
    }


}