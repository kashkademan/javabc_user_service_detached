package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;

@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;
    private final UserContext userContext;

    @Override
    public GoalDto create(CreateGoalDto createGoalDto) {
        User user = userRepository.getByIdOrThrow(userContext.getUserId());
        if (user.getGoals().size() > 1) {
            throw new
        }

        Goal goal = goalMapper.toGoal(createGoalDto);
        goal.setMentor(userRepository.getByIdOrThrow(createGoalDto.mentorId()));
        goal.setUsers(userRepository.findAll());
    }

    @Override
    public GoalDto update(long goalId, UpdateGoalDto updateGoalDto) {
        return null;
    }
}
