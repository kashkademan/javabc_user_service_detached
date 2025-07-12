package school.faang.user_service.controller.goal;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping
    public GoalDto createGoal(@RequestBody @Valid GoalDto goalDto) {
        return goalService.createGoal(goalDto);
    }

    @PutMapping
    public GoalDto updateeGoal(@RequestBody GoalDto goalDto) {
        return goalService.updateGoal(goalDto);
    }

    @DeleteMapping("/{goalId}")
    public void deleteGoal(@PathVariable long goalId) {
        goalService.deleteGoal(goalId);
    }

    @GetMapping("/{goalId}/subtasks")
    public List<GoalDto> findSubtasksByGoalId(@PathVariable Long goalId, @RequestBody GoalFilterDto filterDto) {
        return goalService.findSubtasksByGoalId(goalId, filterDto);        
    }

    @GetMapping("/user/{userId}")
    public List<GoalDto> getGoalsByUser(@PathVariable Long userId, @RequestBody GoalFilterDto filter) {
        return goalService.findGoalsByUserId(userId, filter);
    }

    @GetMapping("/{goalId}")
    public GoalDto getGoalById(@PathVariable Long goalId) {
        return goalService.getGoalById(goalId);
    }

}
