package school.faang.user_service.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.user.UserService;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserScoreJob {
    private static final String USER_SCORE_KEY_PREFIX = "UserScores:";
    private static final String LEADERBOARD_KEY = "Leaderboard";
    private static final String HASH_KEY_PREFIX = "scoreDelta";

    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Scheduled(fixedRate = 5 * 60 * 1000)
    protected void executeInternal() {
        Set<String> keys = redisTemplate.keys(USER_SCORE_KEY_PREFIX + "*");

        if (keys.isEmpty()) {
            log.info("UserScoreJob: нет изменений рейтинга для обработки");
            return;
        }

        for (String key : keys) {
            try {
                String userIdStr = key.replace(USER_SCORE_KEY_PREFIX, "");
                long userId = Long.parseLong(userIdStr);

                Object scoreValue = redisTemplate.opsForHash().get(key, HASH_KEY_PREFIX);
                if (!(scoreValue instanceof String)) {
                    log.warn("Некорректный рейтинг у пользователя {}: {}", userId, scoreValue);
                    continue;
                }

                int scoreDelta = Integer.parseInt((String) scoreValue);
                if (scoreDelta == 0) continue;

                userService.incrementUserScore(userId, scoreDelta);

                updateLeaderboard(userId, scoreDelta);

                redisTemplate.delete(key);
                log.info("Обновлён рейтинг пользователя {} на {}", userId, scoreDelta);
            } catch (Exception e) {
                log.error("Ошибка при обновлении рейтинга", e);
            }
        }
    }

    private void updateLeaderboard(long userId, int scoreDelta) {
        Double currentScore = redisTemplate.opsForZSet().score(LEADERBOARD_KEY, userId);
        double newScore = (currentScore != null ? currentScore : 0) + scoreDelta;

        redisTemplate.opsForZSet().add(LEADERBOARD_KEY, userId, newScore);
    }
}
