package school.faang.user_service.controller.goal;

import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;

public class GoalController {
    private GoalService goalService;

    public GoalDto create(CreateGoalDto createGoalDto) {
        return goalService.create(createGoalDto);
    }
}
