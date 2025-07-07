package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;

import java.util.List;

public interface GoalService {

    GoalDto create(GoalCreateDto goalCreateDto);

    GoalDto update(long goalId, GoalUpdateDto goalUpdateDto);

    GoalDto getById(long goalId);

    void delete(long goalId);

    List<GoalDto> getByFilters(GoalFilterDto filters);
}
