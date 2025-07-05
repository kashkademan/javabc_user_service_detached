package school.faang.user_service.controller.goal;

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
@RequestMapping("${spring.api.base-path-goal}/goal")
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

    public void deleteGoal(long goalId) {
        goalService.deleteGoal();
    }

    public void findSubtasksByGoalId(long goalId) {
        goalService.findSubtasksByGoalId();        
    }

    public void getGoalsByUser(Long userId, GoalFilterDto filter) {
        goalService.findGoalsByUserId();
    }

    @GetMapping("/{goalId}")
    public GoalDto getGoalById(@PathVariable Long goalId) {
        return goalService.getGoalById(goalId);
    }

}
