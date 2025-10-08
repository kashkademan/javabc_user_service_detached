package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalCreateByMentorDto;
import school.faang.user_service.dto.goal.GoalCreateByUserDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/goals")
@RestController
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/create-by-user")
    public GoalDto createGoalByUser(@Valid @RequestBody GoalCreateByUserDto goalCreateByUserDto) {
        return goalService.createByUser(goalCreateByUserDto);
    }

    @PostMapping("create-by-mentor")
    public GoalDto createGoalByMentor(@Valid @RequestBody GoalCreateByMentorDto goalCreateByMentorDto) {
        return goalService.createByMentor(goalCreateByMentorDto);
    }

    @DeleteMapping("/{goalId}")
    public void deleteGoal(@PathVariable long goalId) {
        goalService.delete(goalId);
    }

    @PostMapping("/find-all")
    public List<GoalDto> getByFilters(@Valid @RequestBody GoalFilterDto filters) {
        return goalService.getByFilters(filters);
    }

    @PatchMapping("/{goalId}")
    public GoalDto update(@PathVariable long goalId, @Valid @RequestBody GoalUpdateDto goalUpdateDto) {
        return goalService.update(goalId, goalUpdateDto);
    }
}
