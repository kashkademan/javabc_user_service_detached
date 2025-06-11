package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.CreateGoalRequestDto;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequestMapping("/goals")
@Validated
public class GoalController {
    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    public GoalDto createGoal(@RequestBody @Valid CreateGoalRequestDto request) {
        return goalService.createGoal(request);
    }

    @PutMapping("/{goalId}")
    public GoalDto updateGoal(@PathVariable("goalId")
                              @Min(value = 1, message = "id must be a positive number") Long goalId,
                              @RequestBody @Valid GoalDto  goalDto) {
        return goalService.updateGoal(goalId, goalDto);
    }

    @DeleteMapping("/{goalId}")
    public void deleteGoal(@PathVariable @Min(value = 1, message = "id must be a positive number") long goalId) {
        goalService.deleteGoal(goalId);
    }

    @PostMapping("/{goalId}/subtasks")
    public List<GoalDto> findSubtasksByGoalId(@PathVariable("goalId")
                                              @Min(value = 1, message = "id must be a positive number") long goalId,
                                              @RequestBody GoalFilterDto filter) {
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

    @PostMapping("/user/{userId}")
    public List<GoalDto> getGoalsByUser(@PathVariable("userId")
                                        @Min(value = 1, message = "id must be a positive number") Long userId,
                                        @RequestBody GoalFilterDto filter) {
        return goalService.getGoalsByUser(userId, filter);
    }
}
