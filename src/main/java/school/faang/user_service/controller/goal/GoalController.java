package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public GoalDto create(@Valid @RequestBody CreateGoalDto createGoalDto) {
        log.info("Goal with title {} was created successfully", createGoalDto.title());
        return goalService.create(createGoalDto);
    }

    @PutMapping("/{goalId}")
    @ResponseStatus(HttpStatus.OK)
    public GoalDto update(@PathVariable long goalId, @Valid @RequestBody UpdateGoalDto updateGoalDto) {
        log.info("Goal with id {} was updated successfully", goalId);
        return goalService.update(goalId, updateGoalDto);
    }

    @DeleteMapping("/{goalId}")
    public void deleteGoal(@PathVariable long goalId) {
        log.info("Goal with id {} was deleted", goalId);
        goalService.deleteGoal(goalId);
    }

    @DeleteMapping("users/{goalId}/{userId}")
    public void deleteGoalFromUser(@PathVariable long goalId, @PathVariable long userId) {
        goalService.deleteGoalFromUser(goalId, userId);
    }

    @GetMapping
    public List<GoalDto> getByFilters(@Valid @ModelAttribute GoalFilterDto filters) {
        return goalService.getByFilters(filters);
    }
}
