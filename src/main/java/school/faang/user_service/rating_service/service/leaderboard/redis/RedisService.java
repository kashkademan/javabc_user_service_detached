package school.faang.user_service.rating_service.service.leaderboard.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Value("${app.redis.keys.leaderboard}")
    private String leaderboardKey;

    private final RedisTemplate<String, Long> redisZset;

    private void createRedisUserScore(Long userId, double score) {
        redisZset.opsForZSet().add(leaderboardKey, userId, score);
        log.debug("Пользователь id={} был добавлен в кэш", userId);
    }

    private void incrementRedisUserScore(Long userId, double increment) {
        redisZset.opsForZSet().incrementScore(leaderboardKey, userId, increment);
        log.info("У пользователя id={} увеличилось кол-во баллов на score={}", userId, increment);
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