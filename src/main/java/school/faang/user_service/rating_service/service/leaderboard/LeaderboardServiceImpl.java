package school.faang.user_service.rating_service.service.leaderboard;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.rating_service.repository.UserScoreRepository;
import school.faang.user_service.rating_service.service.leaderboard.postgres.PostgresService;
import school.faang.user_service.rating_service.service.leaderboard.redis.RedisService;

/**
 * Сервис для управления балло-рейтинговой системы
 *
 * @author Linempy
 * @since 29.08.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private final PostgresService postgresService;
    private final RedisService redisService;
    private final UserScoreRepository scoreRepository;

}