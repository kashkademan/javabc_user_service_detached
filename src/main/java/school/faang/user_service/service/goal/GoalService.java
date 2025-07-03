package school.faang.user_service.service.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;

@Component
public interface GoalService {

    GoalDto create(CreateGoalDto createGoalDto);

    GoalDto update(long goalId, UpdateGoalDto updateGoalDto);

    GoalDto getById(long goalId);
}
