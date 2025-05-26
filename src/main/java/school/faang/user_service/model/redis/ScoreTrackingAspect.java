package school.faang.user_service.model.redis;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

import static school.faang.user_service.model.redis.ActionType.FINISH_EVENT;
import static school.faang.user_service.model.redis.ActionType.FINISH_GOAL;
import static school.faang.user_service.model.redis.ActionType.GET_RECOMMENDATION;
import static school.faang.user_service.model.redis.ActionType.OPEN_PROFILE;

@Component
@Aspect
@RequiredArgsConstructor
public class ScoreTrackingAspect {
    private final RedisTemplate<String, Object> redisTemplate;
    private final Map<ActionType, Integer> actionScores = Map.of(
            FINISH_GOAL, 10,
            FINISH_EVENT, 2,
            OPEN_PROFILE, 4,
            GET_RECOMMENDATION, 6
    );

    @Around("@annotation(TrackActionScore)")
    public Object trackScore(ProceedingJoinPoint joinPoint, TrackActionScore trackActionScore) throws Throwable {
        Object result = joinPoint.proceed();

        ActionType type = trackActionScore.value();
        switch (type) {
            case FINISH_GOAL -> {
                return 0;
            }
        }

        int score = actionScores.getOrDefault(type, 0);
        Object[] args = joinPoint.getArgs();
        long userId = 0;
        for (Object arg : args) {
            if (arg instanceof Long) {
                userId = (Long) arg;
                break;
            }
        }

        updateUserScore(userId, score);
        updateLeaderboard(userId, score);

        return result;
    }

    //TODO: подумать как суммировать рейтинг каждого пользователя
    //В кэше или в jobe
    private void updateUserScore(long userId, int scoreDelta) {
        String key = "UserScores:" + userId;
        redisTemplate.opsForHash().put(key, "scoreDelta", scoreDelta);
    }

    private void updateLeaderboard(long userId, int scoreDelta) {
        String key = "Leaderboard";
        Double currentScore = redisTemplate.opsForZSet().score(key, userId);
        double newScore = (currentScore != null ? currentScore : 0) + scoreDelta;

        redisTemplate.opsForZSet().add(key, userId, newScore);
    }
}
