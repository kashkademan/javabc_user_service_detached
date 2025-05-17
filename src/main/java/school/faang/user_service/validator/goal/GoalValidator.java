package school.faang.user_service.validator.goal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.Goal;
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

    public void checkGoalIsCompleted(Goal goal) {
        if (Objects.equals(goal.getStatus(), GoalStatus.COMPLETED)) {
            String errorMsg = String.format("Trying change the completed goal with id %d", goal.getId());
            log.error(errorMsg);
            throw new GoalAlreadyCompletedException(errorMsg);
        }
    }
}
