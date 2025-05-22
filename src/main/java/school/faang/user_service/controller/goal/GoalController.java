package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.CreateGoalRequestDto;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequestMapping("/goals")
public class GoalController {
    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    public GoalDto createGoal(@RequestBody @Valid CreateGoalRequestDto request) {
        validateGoal(request.title());
        return goalService.createGoal(request);
    }

    @PutMapping("/{goalId}")
    public GoalDto updateGoal(@PathVariable("goalId") Long goalId, @RequestBody @Valid GoalDto  goalDto) {
        validateGoal(goalDto.title());
        return goalService.updateGoal(goalId, goalDto);
    }

    @DeleteMapping("/{goalId}")
    public void deleteGoal(@PathVariable long goalId) {
        goalService.deleteGoal(goalId);
    }

    @GetMapping("/{goalId}/subtasks")
    public List<GoalDto> findSubtasksByGoalId(@PathVariable("goalId") long goalId, @ModelAttribute GoalFilterDto filter) {
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

    @GetMapping("/user/{userId}")
    public List<GoalDto> getGoalsByUser(@PathVariable("userId") Long userId, @ModelAttribute GoalFilterDto filter) {
        return goalService.getGoalsByUser(userId, filter);
    }

    private void validateGoal(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("The goal must have a name");
        }
    }
}
