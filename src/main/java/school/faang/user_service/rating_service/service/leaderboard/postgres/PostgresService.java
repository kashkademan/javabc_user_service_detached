package school.faang.user_service.rating_service.service.leaderboard.postgres;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.rating_service.dto.UserScoreProjection;
import school.faang.user_service.rating_service.entity.ScorableEvent;
import school.faang.user_service.rating_service.repository.UserActionLogRepository;
import school.faang.user_service.rating_service.repository.UserScoreRepository;
import school.faang.user_service.rating_service.service.leaderboard.LeaderboardService;

/**
 * Сервис для обращения в Postgres. Используется в {@link LeaderboardService}
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
        scoreRepository.upsertScore(event.getUserId(), score);
        saveUserActionLog(event, score);
        log.info("Баллы пользователя id={} были обновлены", event.getUserId());
    }

    public Page<UserScoreProjection> getTopScores(int limit, int offset) {
        Pageable pageable = PageRequest.of(offset / limit, limit);
        return scoreRepository.findTopScores(pageable);
    }

    public Double getUserScore(Long userId) {
        return scoreRepository.findScoreByUserId(userId);
    }

    private void saveUserActionLog(ScorableEvent event, Double score) {
        logRepository.save(event.getUserId(), event.getActionType().name(), score);
        log.info("Действие пользователя id={} было сохранено", event.getUserId());
    }

}