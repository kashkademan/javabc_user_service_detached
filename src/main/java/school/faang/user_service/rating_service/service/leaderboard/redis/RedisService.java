package school.faang.user_service.rating_service.service.leaderboard.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
@RequiredArgsConstructor
public class RedisService {

    @Value("${redis.keys.leaderboard}")
    private String leaderboardKey;

    @Qualifier("zsetRedisTemplate")
    private final RedisTemplate<String, Object> redisZset;

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
        return redisZset.opsForZSet().score(leaderboardKey, userId);
    }

    public boolean isExistsUserInCache(Long userId) {
        Double score = redisZset.opsForZSet().score(leaderboardKey, userId);
        return score != null;
    }
}