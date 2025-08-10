package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;

import static school.faang.user_service.entity.goal.GoalStatus.ACTIVE;

@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;
    private final UserContext userContext;

    @Override
    public GoalDto create(CreateGoalDto createGoalDto) {
        User user = userRepository.getByIdOrThrow(userContext.getUserId());

        if (!user.getMentees().containsAll(createGoalDto.userIds())
                && createGoalDto.userIds().contains(createGoalDto.mentorId())) {
            throw new DataValidationException("создать цель может либо ментор для своих менти, " +
                    "либо пользователь сам для себя");
        }

        if (!hasNoMoreThanTwoActiveGoals(user)) {
            throw new DataValidationException("Слишком много активных целей!");
        }

        Goal goal = goalMapper.toGoal(createGoalDto);
        goal.setMentor(userRepository.getByIdOrThrow(createGoalDto.mentorId()));
        goal.setUsers(userRepository.findAll());
    }


    private boolean hasNoMoreThanTwoActiveGoals(User user) {
        long activeCount = user.getGoals().stream()
                .filter(goal -> goal.getStatus() == GoalStatus.ACTIVE)
                .limit(3)
                .count();
        return activeCount <= 2;
    }


    @Override
    public GoalDto update(long goalId, UpdateGoalDto updateGoalDto) {
        return null;
    }
}