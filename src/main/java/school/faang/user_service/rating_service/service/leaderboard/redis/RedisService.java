package school.faang.user_service.rating_service.service.leaderboard.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import school.faang.user_service.rating_service.service.leaderboard.postgres.PostgresService;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * RedisService — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
 *
 * @author Linempy
 * @since 05.09.2025
 */
@Slf4j
@Service
public class RedisService {

    @Value("${redis.keys.leaderboard}")
    private String leaderboardKey;

    private final RedisTemplate<String, Object> redisZset;

    private final PostgresService postgresService;

    public RedisService(@Qualifier("zsetRedisTemplate") RedisTemplate<String, Object> redisZset,
                        PostgresService postgresService) {
        this.redisZset = redisZset;
        this.postgresService = postgresService;
    }

    public void loadScores(Map<Long, Double> userScores) {
        if (userScores.isEmpty()) {
            return;
        }

        long startTime = System.currentTimeMillis();

        Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<>();

        for (Map.Entry<Long, Double> entry : userScores.entrySet()) {
            tuples.add(new DefaultTypedTuple<>(
                    entry.getKey(),
                    entry.getValue()
            ));
        }

        redisZset.opsForZSet().add(leaderboardKey, tuples);

        long duration = System.currentTimeMillis() - startTime;
        log.info("Загружено {} записей за {} мс", userScores.size(), duration);
    }

    public void incrementOrCreateUserScore(Long userId, Double earnedScore) {
        Double score = redisZset.opsForZSet().incrementScore(leaderboardKey, userId, earnedScore);

        if (score != null) {
            boolean isNewUser = Math.abs(score - earnedScore) < 0.001;
            log.info("Пользователь id={} {} score={}", userId, isNewUser ? "создан с" : "обновил баллы", score);
            return;
        }

        log.warn("Ошибка в обновлении баллов у пользователя id={}", userId);
    }

    public void getTopUsers(int count) {
        redisZset.opsForZSet().reverseRangeWithScores(leaderboardKey, 0, count - 1);
    }

    public Double getScoreByUserId(Long userId) {
        Double score = redisZset.opsForZSet().score(leaderboardKey, userId);

        if (score != null) {
            return score;
        }

        score = postgresService.getUserScore(userId);

        if (score != null) {
            redisZset.opsForZSet().add(leaderboardKey, userId, score);
            return score;
        }

        return (double) 0;
    }

    public void clearLeaderboard() {
        redisZset.delete(leaderboardKey);
        log.info("Таблица лидеров была очищена");
    }

    public boolean isExistsUserInCache(Long userId) {
        Double score = redisZset.opsForZSet().score(leaderboardKey, userId);
        return score != null;
    }
}