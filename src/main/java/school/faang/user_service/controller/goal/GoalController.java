package school.faang.user_service.controller.goal;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

@RestController
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public void createGoal(Long userId, Goal goal) {
        goalService.createGoal();
    }

    public void updateeGoal(Long goalId, GoalDto goal) {
        goalService.updateGoal();
    }

    public void deleteGoal(long goalId) {
        goalService.deleteGoal();
    }

    public void findSubtasksByGoalId(long goalId) {
        goalService.findSubtasksByGoalId();        
    }

    public void getGoalsByUser(Long userId, GoalFilterDto filter) {
        goalService.findGoalsByUserId();
    }

}
