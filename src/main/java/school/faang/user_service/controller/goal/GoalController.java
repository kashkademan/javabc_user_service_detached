package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.facade.GoalMapping;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalService;
    private final GoalMapping goalMapping;

    @PostMapping
    GoalDto create(@Valid @RequestBody CreateGoalDto createGoalDto) {
        return goalMapping.mappingForCreate(createGoalDto);
    }

    @PatchMapping("/{goalId}")
    GoalDto update(@PathVariable long goalId, @Valid @RequestBody UpdateGoalDto updateGoalDto) {
        return goalMapping.mappingForUpdate(goalId, updateGoalDto);
    }

    @DeleteMapping("/{goalId}")
    void delete(@PathVariable long goalId) {
        goalService.delete(goalId);
    }

    @GetMapping()
    List<GoalDto> getByFilters(@Valid @RequestBody GoalFilterDto filters) {
        return goalMapping.mappingForFilters(filters);
    }
}
