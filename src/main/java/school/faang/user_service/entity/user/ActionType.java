package school.faang.user_service.entity.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import school.faang.user_service.dto.goal.GoalUpdateDto;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public enum ActionType {
    GOAL_CREATED_BY_USER(jp -> true),
    GOAL_CREATED_BY_MENTOR(jp -> true),
    CAREER_ADDED(jp -> true),
    GOAL_COMPLETED(ActionType::checkGoalCompleted);

    private final Predicate<JoinPoint> condition;

    public boolean shouldAddScore(JoinPoint joinPoint) {
        return condition.test(joinPoint);
    }

    private static boolean checkGoalCompleted(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof GoalUpdateDto dto) {
                return dto.status() == GoalStatus.COMPLETED;
            }
        }
        return false;
    }
}
