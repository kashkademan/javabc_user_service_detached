package school.faang.user_service.controller.goal;

import org.springframework.web.bind.annotation.PostMapping;
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
    public void createGoal(@RequestBody @Valid GoalDto goalDto) {
        goalService.createGoal(goalDto);
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
