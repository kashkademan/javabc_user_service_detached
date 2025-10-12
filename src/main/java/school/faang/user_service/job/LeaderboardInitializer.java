package school.faang.user_service.job;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.LeaderBoardConfig;
import school.faang.user_service.dto.user.LeaderScoreDto;
import school.faang.user_service.repository.user.UserScoreRepository;
import school.faang.user_service.service.user.LeaderBoardCacheService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LeaderboardInitializer {

    private final UserScoreRepository userScoreRepository;
    private final LeaderBoardCacheService leaderBoardCacheService;
    private final LeaderBoardConfig leaderBoardConfig;

    @PostConstruct
    public void initLeaderboard() {
        String redisKey = leaderBoardConfig.getRedisKey();

        if (leaderBoardCacheService.exists(redisKey)) {
            log.info("Leaderboard already initialized in Redis, skipping.");
            return;
        }

        int limit = leaderBoardConfig.getSize();
        List<LeaderScoreDto> leaderboard = userScoreRepository
                .getLeaderBoard(PageRequest.of(0, limit))
                .getContent();

        if (leaderboard.isEmpty()) {
            log.info("No user score data found in database — leaderboard not initialized.");
            return;
        }

        leaderboard.forEach(entry ->
                leaderBoardCacheService.addScore(entry.username(), entry.points())
        );

        log.info("Leaderboard initialized in Redis with {} entries.", leaderboard.size());
    }
}
