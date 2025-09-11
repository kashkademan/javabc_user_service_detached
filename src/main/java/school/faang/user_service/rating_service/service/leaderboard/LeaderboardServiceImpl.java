package school.faang.user_service.rating_service.service.leaderboard;

import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import school.faang.user_service.rating_service.dto.UserScoreViewDto;
import school.faang.user_service.rating_service.entity.ScorableEvent;
import school.faang.user_service.rating_service.mapper.UserScoreMapper;
import school.faang.user_service.rating_service.service.leaderboard.postgres.PostgresService;
import school.faang.user_service.rating_service.service.leaderboard.redis.RedisService;

import java.util.List;
import java.util.Set;

/**
 * Сервис для управления балльно-рейтинговой системы
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
    private final UserScoreMapper mapper;

    public void processUpdateUserScore(ScorableEvent event, Double earnedScore) {
        try {
            redisService.incrementOrCreateUserScore(event.getUserId(), earnedScore);
            postgresService.upsertUserScore(event, earnedScore);

        } catch (RedisConnectionException e) {
            log.error("Redis упал! Попытка сохранить данные в Postgres", e);
            postgresService.upsertUserScore(event, earnedScore);
        }
    }

    public List<UserScoreViewDto> getTopScores(Integer size, Integer page) {
        Set<ZSetOperations.TypedTuple<Object>> topUsers = redisService.getTopUsers(page, size);
        return topUsers.stream()
                .map(tuple -> mapper.getDtoByFields((Long) tuple.getValue(), tuple.getScore()))
                .toList();
    }

    public UserScoreViewDto getUserScore(Long userId) {
        Double score = redisService.getScoreByUserId(userId);
        return mapper.getDtoByFields(userId, score);
    }
}