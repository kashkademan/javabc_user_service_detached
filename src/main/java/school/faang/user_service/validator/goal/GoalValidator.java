package school.faang.user_service.validator.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.goal.MaxActiveGoalPerUserException;
import school.faang.user_service.exception.goal.UpdateGoalWithActiveSubGoalsException;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GoalValidator {

    @Value("${goal.maxLimit}")
    private Integer goalLimit;
    private final UserContext userContext;
    private final GoalRepository goalRepository;

    public void validateMaxActiveGoalLimitPerUser(long userId) {
        boolean isUserReachActiveGoalLimit = goalRepository.countActiveGoalsPerUser(userId) >= goalLimit;
        if (isUserReachActiveGoalLimit) {
            throw new MaxActiveGoalPerUserException(userContext.getUserId(), goalLimit);
        }
    }

    public void validateAllSubGoalsCompleted(long goalId, Stream<Goal> subGoals) {
        String activeSubGoalIds = subGoals.filter(goal -> !goal.getStatus().equals(GoalStatus.COMPLETED))
                .map(subGoal -> String.valueOf(subGoal.getId()))
                .collect(Collectors.joining(", "));
        if (!activeSubGoalIds.isEmpty()) {
            throw new UpdateGoalWithActiveSubGoalsException(goalId, activeSubGoalIds);
        }
    }
}