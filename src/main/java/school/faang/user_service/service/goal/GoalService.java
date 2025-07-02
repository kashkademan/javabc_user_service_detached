package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;

public interface GoalService {

    GoalDto create(CreateGoalDto createGoalDto);
}
