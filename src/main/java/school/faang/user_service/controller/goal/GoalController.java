package school.faang.user_service.controller.goal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;

@RestController
@RequestMapping("/v1/")
public class GoalController {
    private GoalService goalService;

    public GoalDto create(CreateGoalDto createGoalDto) {
        return goalService.create(createGoalDto);
    }
}
