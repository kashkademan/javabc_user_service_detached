package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.FilterGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.IndexGoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.filter.goal.GoalFilterBuilderInterface;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.policy.goal.GoalCreatePolicy;
import school.faang.user_service.policy.goal.GoalDeletePolicy;
import school.faang.user_service.policy.goal.GoalUpdatePolicy;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final UserContext userContext;
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final UserRepository userRepository;
    private final GoalFilterBuilderInterface<Goal, FilterGoalDto> goalFilter;
    private final GoalCreatePolicy goalCreatePolicy;
    private final GoalUpdatePolicy goalUpdatePolicy;
    private final GoalDeletePolicy goalDeletePolicy;

    @Override
    public GoalDto create(CreateGoalDto createGoalDto) {
        goalCreatePolicy.validate(createGoalDto);
        Goal goal = goalMapper.toGoal(createGoalDto);

        List<User> users = userRepository.findAllById(createGoalDto.userIds());
        User mentor = userRepository.getByIdOrThrow(createGoalDto.mentorId());
        goal.setUsers(users);
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setMentor(mentor);

        goalRepository.save(goal);
        return goalMapper.toGoalDto(goal);
    }

    @Override
    public List<GoalDto> get(IndexGoalDto dto) {
        Specification<Goal> specification = goalFilter.buildSpecification(dto.filters(), null);
        List<Goal> goals = goalRepository.findAll(specification);
        return goalMapper.toGoalDtoList(goals);
    }

    @Override
    public GoalDto update(Long id, UpdateGoalDto updateGoalDto) {
        Goal goal = goalRepository.getByIdOrThrow(id);
        goalUpdatePolicy.validate(updateGoalDto, goal);
        if (updateGoalDto.mentorId() != null) {
            User mentor = userRepository.getByIdOrThrow(updateGoalDto.mentorId());
            goal.setMentor(mentor);
        }

        goalMapper.update(goal, updateGoalDto);
        return goalMapper.toGoalDto(goalRepository.save(goal));
    }

    @Override
    public void delete(Long id) {
        Goal goal = goalRepository.getByIdOrThrow(id);
        goalDeletePolicy.validate(goal);
        long usersSize = goal.getUsers().size();
        long currentUserId = userContext.getUserId();
        boolean isMentor = goal.getMentor() != null
                && goal.getMentor().getId().equals(currentUserId);
        if (isMentor || usersSize <= 1) {
            goalRepository.deleteById(id);
        } else {
            goalRepository.deleteUserFromGoal(currentUserId, goal.getId());
        }
    }
}
