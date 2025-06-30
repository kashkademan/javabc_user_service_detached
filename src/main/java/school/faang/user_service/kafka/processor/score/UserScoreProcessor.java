package school.faang.user_service.kafka.processor.score;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.score.UserScoreChangedEvent;
import school.faang.user_service.service.score.LeaderboardService;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserScoreProcessor {

    private final LeaderboardService leaderboardService;

    public void process(UserScoreChangedEvent changeScoreEvent) {
        long userId = changeScoreEvent.getUserId();
        int newScore = changeScoreEvent.getUpdatedScore();

        if (newScore == 0) {
            log.warn("Событие {} пропущено", changeScoreEvent);
            return;
        }

        leaderboardService.updateLeaderboard(userId, newScore);

        log.info("Очки обновлены: userId={}, newScore={}", userId, newScore);
    }
}
