package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoalServiceImpl implements GoalService {

    @Value("${max-active-goals:2}")
    private int maxActiveGoals;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;
    private final UserContext userContext;
    private final List<GoalFilter> goalFilters;

    @Override
    public GoalDto create(CreateGoalDto createGoalDto) {
        List<Long> menteeIds = userRepository.getByIdOrThrow(userContext.getUserId()).getMentees().stream()
                .map(User::getId)
                .toList();

        if (!createGoalDto.userIds().contains(userContext.getUserId())
                || !createGoalDto.userIds().containsAll(menteeIds)) {
            throw new ForbiddenException("Forbidden to create goal for chosen users");
        }

        long activeGoalsSize = goalRepository.findGoalsByUserId(userContext.getUserId())
                .filter(goal -> goal.getStatus().equals(GoalStatus.ACTIVE))
                .count();

        if (activeGoalsSize >= maxActiveGoals) {
            throw new ForbiddenException(("Forbidden to create goal. User already has %d active goals." +
                    " Max active goals = %d".formatted(maxActiveGoals))
                    .formatted(activeGoalsSize));
        }

        Goal goal = goalMapper.toGoal(createGoalDto);

        if (createGoalDto.mentorId() != null) {
            goal.setMentor(userRepository.getByIdOrThrow(createGoalDto.mentorId()));
        }

        if (createGoalDto.userIds().isEmpty()) {
            throw new DataValidationException("Users cant be empty");
        } else {
            List<User> users = createGoalDto.userIds().stream()
                    .map(userId -> userRepository.getByIdOrThrow(userId)).toList();
            goal.setUsers(users);
        }

        if (createGoalDto.parentId() != null) {
            goal.setParent(goalRepository.getByIdOrThrow(createGoalDto.parentId()));
        }

        goal = goalRepository.save(goal);

        log.info("Goal {} created", goal.getId());
        return goalMapper.toGoalDto(goal);
    }

    @Override
    public void delete(long goalId) {
        GoalDto goalDto = goalMapper.toGoalDto(goalRepository.getByIdOrThrow(goalId));

        if (goalDto.mentorId() == userContext.getUserId()) {
            goalRepository.deleteById(goalId);
            log.info("Goal {} deleted", goalId);
        } else if (goalDto.userIds().contains(userContext.getUserId())) {
            goalRepository.deleteUserFromGoal(userContext.getUserId(), goalId);
            log.info("User {} deleted his goal {}", userContext.getUserId(), goalId);
        } else {
            throw new ForbiddenException("Current user cant delete chosen goal");
        }
    }

    @Override
    public GoalDto update(long goalId, UpdateGoalDto updateGoalDto) {
        Goal goal = goalRepository.getByIdOrThrow(goalId);

        GoalDto goalDto = goalMapper.toGoalDto(goal);

        if (goalDto.status().equals(GoalStatus.COMPLETED)) {
            throw new ForbiddenException("Cant update goal in completed status");
        }

        if (goalDto.mentorId() != userContext.getUserId() || !goalDto.userIds().contains(userContext.getUserId())) {
            throw new ForbiddenException("Current user cant update chosen goal");
        }

        goalMapper.update(updateGoalDto, goal);

        if (updateGoalDto.mentorId() != null) {
            User mentor = userRepository.getByIdOrThrow(updateGoalDto.mentorId());
            goal.setMentor(mentor);
        }
        log.info("Goal {} was updated", goalId);
        return goalMapper.toGoalDto(goalRepository.save(goal));
    }

    @Override
    public List<GoalDto> getByFilters(GoalFilterDto filters) {
        Stream<Goal> goalStream = goalRepository.findAll().stream();

        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(filters)) {
                goalStream = goalFilter.apply(goalStream, filters);
            }
        }
        return goalStream.map(goalMapper::toGoalDto).toList();
    }
}