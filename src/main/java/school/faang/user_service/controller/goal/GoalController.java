package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import school.faang.user_service.entity.filter.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.dto.GoalDto;
import school.faang.user_service.entity.goal.mapper.GoalMapper;
import school.faang.user_service.service.GoalService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;
    private final GoalMapper goalMapper;

    @GetMapping("/{goalId}")
    public ResponseEntity<GoalDto> getGoal(@PathVariable long goalId) {
        Goal goal = goalService.getGoalById(goalId);
        return ResponseEntity.ok(goalMapper.toGoalDto(goal));
    }

    @PostMapping("/filter")
    public ResponseEntity<List<GoalDto>> getGoals(@RequestBody GoalFilterDto goalFilterDto) {
        List<Goal> filteredGoals = goalService.getGoalsByFilter(goalFilterDto);
        return ResponseEntity.ok(goalMapper.toGoalDtoList(filteredGoals));
    }

    @PostMapping("/{parentId}/subGoals/filter")
    public ResponseEntity<List<GoalDto>> getSubGoals(@PathVariable long parentId, @RequestBody GoalFilterDto goalFilterDto) {
        List<Goal> filteredSubGoals = goalService.getSubGoalsByFilter(parentId, goalFilterDto);
        return ResponseEntity.ok(goalMapper.toGoalDtoList(filteredSubGoals));
    }

    @PostMapping
    public ResponseEntity<GoalDto> createGoal(@RequestBody @Valid GoalDto goalDto) {
        Goal createdGoal = goalService.createGoal(
                goalMapper.toGoal(goalDto),
                goalDto.skillsId(),
                goalDto.parentId()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdGoal.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(goalMapper.toGoalDto(createdGoal));
    }

    @PatchMapping("/{goalId}")
    public ResponseEntity<GoalDto> updateGoal(@PathVariable long goalId, @RequestBody @Valid GoalDto goalDto) {
        Goal createdGoal = goalService.update(
                goalId,
                goalMapper.toGoal(goalDto),
                goalDto.skillsId()
        );
        return ResponseEntity.ok(goalMapper.toGoalDto(createdGoal));
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable long goalId) {
        goalService.delete(goalId);
        return ResponseEntity.noContent().build();
    }
}