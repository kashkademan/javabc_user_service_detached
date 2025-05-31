package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.GoalService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    public GoalDto createGoal(Long userId, @Valid GoalDto goalDto) {
        return goalService.createGoal(userId, goalDto);
    }

    public GoalDto updateGoal(Long goalId, @Valid GoalDto goalDto) {
        return goalService.updateGoal(goalId, goalDto);
    }

    public GoalDto deleteGoal(long goalId) {
        return goalService.deleteGoal(goalId);
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, @Valid GoalFilterDto filter) {
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

    public List<GoalDto> getGoalsByUser(Long userId, @Valid GoalFilterDto filter) {
        return goalService.findGoalsByUserId(userId, filter);
    }
}
