package school.faang.user_service.aspect.score;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.User;

import java.util.Map;

import static school.faang.user_service.aspect.util.AspectUtils.requireArgumentOfType;
import static school.faang.user_service.aspect.score.ActionType.COMPLETE_EVENT;
import static school.faang.user_service.aspect.score.ActionType.COMPLETE_GOAL;

@Aspect
@Component
@RequiredArgsConstructor
public class ScoreTrackingAspect {
    private static final String USER_SCORE_KEY_PREFIX = "UserScores:";
    private static final String HASH_KEY_PREFIX = "scoreDelta";

    private final RedisTemplate<String, Object> redisTemplate;

    private final Map<ActionType, Integer> actionScores = Map.of(
            COMPLETE_GOAL, 10,
            COMPLETE_EVENT, 2
    );

    @Around("@annotation(trackActionScore)")
    public Object trackScore(ProceedingJoinPoint joinPoint, TrackActionScore trackActionScore) throws Throwable {
        Object result = joinPoint.proceed();

        ActionType type = trackActionScore.value();
        int score = actionScores.getOrDefault(type, 0);
        switch (type) {
            case COMPLETE_GOAL -> trackAfterCompleteGoal(joinPoint, score);
            case COMPLETE_EVENT -> trackAfterCompleteEvent(joinPoint, score);
        }

        return result;
    }

    private void trackAfterCompleteGoal(ProceedingJoinPoint joinPoint, int scoreDelta) {
        Goal goal = requireArgumentOfType(joinPoint, Goal.class);

        goal.getUsers().stream()
                .map(User::getId)
                .forEach(userId -> incrementUserScoreDelta(userId, scoreDelta));
    }


    private void trackAfterCompleteEvent(ProceedingJoinPoint joinPoint, int scoreDelta) {
        Event event = requireArgumentOfType(joinPoint, Event.class);
        if (event.getStatus() != EventStatus.COMPLETED) {
            return;
        }

        event.getAttendees().stream()
                .map(User::getId)
                .forEach(userId -> incrementUserScoreDelta(userId, scoreDelta));

        incrementUserScoreDelta(event.getOwner().getId(), scoreDelta);
    }

    private void incrementUserScoreDelta(long userId, int scoreDelta) {
        String key = USER_SCORE_KEY_PREFIX + userId;
        Object scoreValue = redisTemplate.opsForHash().get(key, HASH_KEY_PREFIX);

        int currentDelta = 0;
        if (scoreValue instanceof Integer i) {
            currentDelta = i;
        }

        int newDelta = currentDelta + scoreDelta;
        redisTemplate.opsForHash().put(key, HASH_KEY_PREFIX, newDelta);
    }
}
