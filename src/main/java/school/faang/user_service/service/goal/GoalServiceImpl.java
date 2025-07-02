package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;


    @Override
    public GoalDto create(CreateGoalDto createGoalDto) {
        validateCreateGoalDto(createGoalDto);
        Goal goal = goalMapper.toGoal(createGoalDto);

        return null;
    }

    private void validateCreateGoalDto(CreateGoalDto dto) {
        if (dto.title().isBlank()) {
            throw new IllegalArgumentException("goal title can not to be empty");
        }
    }
}
