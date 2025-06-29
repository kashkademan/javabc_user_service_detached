package school.faang.user_service.kafka.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.score.UserScoreChangedEvent;
import school.faang.user_service.service.score.LeaderboardService;
import school.faang.user_service.service.user.UserService;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserScoreProcessor {

    private final LeaderboardService leaderboardService;
    private final UserService userService;

    public void process(UserScoreChangedEvent changeScoreEvent) {
        long userId = changeScoreEvent.getUserId();
        int delta = changeScoreEvent.getScoreDelta();

        if (delta == 0) {
            log.warn("Событие {} пропущено", changeScoreEvent);
            return;
        }

        int userScore = userService.getUserScore(userId);
        leaderboardService.updateLeaderboard(userId, userScore);

        log.info("Очки обновлены: userId={}, delta={}, score={}", userId, delta, userScore);
    }
}
