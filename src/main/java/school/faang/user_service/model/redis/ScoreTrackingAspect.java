package school.faang.user_service.model.redis;

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

import java.util.List;
import java.util.Map;

import static school.faang.user_service.model.redis.ActionType.COMPLETE_EVENT;
import static school.faang.user_service.model.redis.ActionType.COMPLETE_GOAL;

@Aspect
@Component
@RequiredArgsConstructor
public class ScoreTrackingAspect {
    private final RedisTemplate<String, Object> redisTemplate;

    private final Map<ActionType, Integer> actionScores = Map.of(
            COMPLETE_GOAL, 10,
            COMPLETE_EVENT, 2
    );

    @Around("@annotation(TrackActionScore)")
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
        Object[] args = joinPoint.getArgs();
        Goal goal = null;
        for (Object arg : args) {
            if (arg instanceof Goal) {
                goal = (Goal) arg;
                break;
            }
        }

        if (goal == null) {
            return;
        }

        List<Long> userIds = goal.getUsers().stream()
                .map(User::getId)
                .toList();

        userIds.forEach(userId -> updateUserScore(userId, scoreDelta));
    }

    private void trackAfterCompleteEvent(ProceedingJoinPoint joinPoint, int scoreDelta) {
        Object[] args = joinPoint.getArgs();
        Event event = null;
        for (Object arg : args) {
            if (arg instanceof Event) {
                event = (Event) arg;
                break;
            }
        }

        if (event == null || event.getStatus() != EventStatus.COMPLETED) {
            return;
        }

        List<Long> participatedIds = event.getAttendees().stream()
                .map(User::getId)
                .toList();

        participatedIds.forEach(userId -> updateUserScore(userId, scoreDelta));

        Long ownerId = event.getOwner().getId();
        updateUserScore(ownerId, scoreDelta);
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

//    private <T> T injectArgument(ProceedingJoinPoint joinPoint) {
//        Object[] args = joinPoint.getArgs();
//        T object = null;
//        for (Object arg : args) {
//            if (arg instanceof Goal) {
//                object = (T) arg;
//                break;
//            }
//        }
//
//        return object;
//    }
}
