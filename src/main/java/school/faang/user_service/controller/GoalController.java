package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.service.goal.GoalService;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/fanng")
public class GoalController {

    private final GoalService goalService;

    @PostMapping("/goals")
    public GoalDto create(@Valid @RequestBody CreateGoalDto createGoalDto) {
        return goalService.create(createGoalDto);
    }

    @PatchMapping("/goals/{goalId}")
    public GoalDto update(@PathVariable @Min(1) long goalId,
                          @Valid @RequestBody UpdateGoalDto updateGoalDto) {
        return goalService.update(goalId, updateGoalDto);
    }

    @DeleteMapping("/goals/{goalId}")
    public void delete(@PathVariable @Min(1) long goalId) {
        goalService.delete(goalId);
    }
}