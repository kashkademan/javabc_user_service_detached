package school.faang.user_service.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.goal.CountActiveGoalMoreMaxException;
import school.faang.user_service.exception.goal.GoalAlreadyCompletedException;

import java.util.Objects;

@Component
@Slf4j
public class GoalValidator {
    private static final int MAX_NUM_ACTIVE_GOAL_FOR_USER = 3;
    public void checkCountGoalForUser(long userId, int countActiveGoalForUser) {
        log.warn("Count active goal for user with id {} {}", userId, countActiveGoalForUser);

        if (countActiveGoalForUser > MAX_NUM_ACTIVE_GOAL_FOR_USER) {
            log.error("Count active goal more max, max goal {}", MAX_NUM_ACTIVE_GOAL_FOR_USER);
            throw new CountActiveGoalMoreMaxException(MAX_NUM_ACTIVE_GOAL_FOR_USER);
        }
    }

    public void checkGoalIsCompleted(long goalId, GoalStatus goalStatus) {
        if (Objects.equals(goalStatus, GoalStatus.COMPLETED)) {
            String errorMsg = String.format("Trying change the completed goal with id %d", goalId);
            log.error(errorMsg);
            throw new GoalAlreadyCompletedException(errorMsg);
        }
    }
}
