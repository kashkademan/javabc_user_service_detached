package school.faang.user_service.service.goal;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.repository.user.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationService {
    private final UserContext userContext;
    private final UserRepository userRepository;

    public void validatePossibleToCreateOrUpdate(Goal goal) {
        boolean userNotAllowed = !ablePersonToCreateGoal(goal);
        boolean noAvailablePerformers = getAvailablePerformers(goal).isEmpty();
        boolean goalStatusCompleted = isStatusCompleted(goal);
        boolean userAllowed = canUserUpdateGoal(goal);
        if (userNotAllowed || noAvailablePerformers || goalStatusCompleted || !userAllowed) {
            log.error("User is not allowed or no available performers for goal {}", goal.getId());
            throw new ForbiddenException("You cannot create or update this goal");
        }
    }

    private boolean ablePersonToCreateGoal(Goal goal) {
        long userId = userContext.getUserId();
        boolean isMentor = goal.getMentor() != null && userId == goal.getMentor().getId();
        boolean assignForHimself = goal.getUsers() != null && goal.getUsers().stream()
                .anyMatch(user -> userId == user.getId());

        return isMentor || assignForHimself;
    }

    private List<Long> getAvailablePerformers(Goal goal) {
        List<User> users = userRepository.findAllByIdIn(goal.getUsers().stream()
                .map(User::getId)
                .toList());
        return users.stream().filter(user -> user.getGoals().size() < 2).map(User::getId).toList();
    }

    private boolean isStatusCompleted(Goal goal) {
        return goal.getStatus().equals(GoalStatus.COMPLETED);
    }

    private boolean hasGoalMentor(Goal goal) {
        return goal.getMentor() != null;
    }

    private boolean canUserUpdateGoal(Goal goal) {
        if (!hasGoalMentor(goal)) {
            return true;
        }
        return userContext.getUserId() == goal.getMentor().getId();
    }
}
