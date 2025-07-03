package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.IndexGoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;

import java.util.List;

public interface GoalService {
    GoalDto create(CreateGoalDto createGoalDto);

    List<GoalDto> get(IndexGoalDto dto);

    void delete(Long id);

    GoalDto update(Long id, UpdateGoalDto updateGoalDto);
}
