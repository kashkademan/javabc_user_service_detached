package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping
    public void createGoal(@RequestBody Long userId, @RequestBody Goal goal) {
        validateGoal(goal);
        goalService.createGoal(userId, goal);
    }

    @PutMapping("/{goalId}")
    public void updateGoal(@PathVariable Long goalId, @RequestBody Goal goal) {
        validateGoal(goal);
        goalService.updateGoal(goalId, goal);
    }

    @DeleteMapping("/{goalId}")
    public void deleteGoal(@PathVariable long goalId) {
        goalService.deleteGoal(goalId);
    }

    @GetMapping("/{goalId}/subtasks")
    public List<GoalDto> findSubtasksByGoalId(@PathVariable long goalId, GoalFilterDto filter) {
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

    @GetMapping("/user/{userId}")
    public List<GoalDto> getGoalsByUser(@PathVariable Long userId, GoalFilterDto filter) {
        return goalService.getGoalsByUser(userId, filter);
    }

    private void validateGoal(Goal goal) {
        if (goal.getTitle() == null || goal.getTitle().isEmpty()) {
            throw new IllegalArgumentException("The goal must have a name");
        }
    }
}
