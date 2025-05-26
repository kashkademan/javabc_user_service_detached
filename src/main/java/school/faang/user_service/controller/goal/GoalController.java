package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.GoalService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/goal")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/{userId}/create")
    public GoalDto createGoal(@PathVariable Long userId, @RequestBody GoalDto goalDto) {
        if (goalDto.getTitle().isBlank()) throw new IllegalArgumentException("Goal has no title");
        GoalDto created = goalService.createGoal(userId, goalDto);
        log.info("Goal was created for user {}. Id {}, title {}", userId, created.getId(), created.getTitle());
        return created;
    }

    @PutMapping("/{goalId}/update")
    public GoalDto updateGoal(@PathVariable Long goalId, @RequestBody GoalDto goalDto) {
        if (goalDto.getTitle().isBlank()) throw new IllegalArgumentException("Goal has no title");
        GoalDto updated = goalService.updateGoal(goalId, goalDto);
        log.info("Goal id {} was updated", updated.getId());
        return updated;
    }

    @DeleteMapping("/{goalId}")
    public GoalDto deleteGoal(@PathVariable Long goalId) {
        GoalDto deleted = goalService.deleteGoal(goalId);
        log.info("Goal was deleted, id {}, title {}", deleted.getId(), deleted.getTitle());
        return deleted;
    }

    @PostMapping("/{goalId}/subtasks")
    public List<GoalDto> findSubtasksByGoalId(@PathVariable Long goalId, @RequestBody GoalFilterDto filter) {
        return goalService.findSubtasksByGoalId(goalId, filter);
    }

    @PostMapping("/ofuser/{userId}")
    public List<GoalDto> getGoalsByUser(@PathVariable Long userId, @RequestBody GoalFilterDto filter) {
        return goalService.findGoalsByUserId(userId, filter);
    }
}
