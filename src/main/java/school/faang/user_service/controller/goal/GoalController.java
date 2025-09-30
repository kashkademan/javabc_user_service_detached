package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;

@RestController
@RequestMapping("/goals")
@Slf4j
@RequiredArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping
    public GoalDto create(@Valid @RequestBody CreateGoalDto createGoalDto) {
        return goalService.create(createGoalDto);
    }
}
