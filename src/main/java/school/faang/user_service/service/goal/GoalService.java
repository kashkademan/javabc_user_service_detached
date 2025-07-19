package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;

public interface GoalService {
    GoalDto create(CreateGoalDto createGoalDto);
}
