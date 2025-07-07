package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalUpdateDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.FilterService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private static final String USER_HAS_NO_ACCESS = "user has no access to provided goal";
    private static final String USER_HAS_NO_ACCESS_TO_CREATE = "user has no access to create goal for provided users";
    private static final String USER_HAS_TO_MANY_ACTIVE_GOALS = "user has too many ACTIVE goals";
    private static final String GOAL_COMPLETED = "goal completed";
    private static final String MENTOR_HAS_NO_MENTEES = "mentor has no mentees";

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final UserContext userContext;
    private final FilterService<Goal, GoalFilterDto> filterService;

    @Override
    @Transactional
    public GoalDto create(GoalCreateDto goalCreateDto) {
        long userId = userContext.getUserId();
        Goal goal = goalMapper.toGoal(goalCreateDto);
        if (goalCreateDto.parentId() != null) {
            goal.setParent(
                    goalRepository.getByIdOrThrow(
                            goalCreateDto.parentId()
                    )
            );
        }

        boolean userIsMentor = false;
        if (goalCreateDto.mentorId() != null) {
            userIsMentor = (userId == goalCreateDto.mentorId());
            if (!userIsMentor) {
                throw new ForbiddenException(USER_HAS_NO_ACCESS);
            }
            goal.setMentor(
                    userRepository.getByIdOrThrow(
                            goalCreateDto.mentorId()
                    )
            );
        }

        User user = userRepository.getByIdOrThrow(userId);
        goal.setUsers(
                getUsersForGoalOrThrow(
                        userIsMentor, user, goalCreateDto.userIds()
                )
        );
        goal = goalRepository.save(goal);
        return goalMapper.toGoalDto(goal);
    }

    private List<User> getUsersForGoalOrThrow(boolean isMentor, User user, List<Long> userIds) {
        List<User> users = List.of(user);

        if (isMentor) {
            users = user.getMentees();
            if (users == null || users.isEmpty()) {
                throw new ForbiddenException(MENTOR_HAS_NO_MENTEES);
            }
        }

        List<User> usersForGoal = new ArrayList<>();
        for (Long id : userIds) {
            User userForGoal = users.stream()
                    .filter(u -> u.getId().equals(id))
                    .findAny()
                    .orElseThrow(() -> new ForbiddenException(USER_HAS_NO_ACCESS_TO_CREATE));

            if (userForGoal.getGoals() == null || userForGoal.getGoals().isEmpty()) {
                usersForGoal.add(userForGoal);
                continue;
            }
            long activeGoalsCount = userForGoal.getGoals().stream()
                    .filter(g -> g.getStatus().equals(GoalStatus.ACTIVE))
                    .count();
            if (activeGoalsCount > 1) {
                throw new DataValidationException(USER_HAS_TO_MANY_ACTIVE_GOALS);
            }
            usersForGoal.add(userForGoal);
        }
        return usersForGoal;
    }

    @Override
    @Transactional
    public GoalDto update(long goalId, GoalUpdateDto goalUpdateDto) {
        long userId = userContext.getUserId();

        Goal goal = goalRepository.getByIdOrThrow(goalId);
        if (!isUserGoalParticipant(userId, goal)) {
            throw new ForbiddenException(USER_HAS_NO_ACCESS);
        }

        if (GoalStatus.COMPLETED.equals(goal.getStatus())) {
            throw new IllegalStateException(GOAL_COMPLETED);
        }

        goalMapper.update(goalUpdateDto, goal);
        goal = goalRepository.save(goal);
        return goalMapper.toGoalDto(goal);
    }

    @Override
    @Transactional
    public GoalDto getById(long goalId) {
        long userId = userContext.getUserId();
        Goal goal = goalRepository.getByIdOrThrow(goalId);
        if (!isUserGoalParticipant(userId, goal)) {
            throw new ForbiddenException(USER_HAS_NO_ACCESS);
        }
        return goalMapper.toGoalDto(goal);
    }

    @Override
    @Transactional
    public void delete(long goalId) {
        long userId = userContext.getUserId();
        Goal goal = goalRepository.getByIdOrThrow(goalId);
        boolean hasMentor = goal.getMentor() != null;
        if (!isUserGoalParticipant(userId, goal)) {
            throw new ForbiddenException(USER_HAS_NO_ACCESS);

        }

        long usersCount = goal.getUsers() == null ? 0 :
                goal.getUsers().stream()
                        .map(User::getId)
                        .collect(Collectors.toSet())
                        .size();

        if (hasMentor && goal.getMentor().getId() == userId || usersCount == 1) {
            goalRepository.deleteById(goalId);
            return;
        }

        goalRepository.deleteUserFromGoal(userId, goalId);
    }

    @Override
    @Transactional
    public List<GoalDto> getByFilters(GoalFilterDto filterDto) {
        long userId = userContext.getUserId();
        List<Goal> goals = goalRepository.findGoalsByUserId(userId).toList();
        goals = filterService.toList(goals, filterDto);
        return goals.stream()
                .map(goalMapper::toGoalDto)
                .toList();
    }

    private boolean goalContainsUser(Goal goal, long userId) {
        if (goal.getUsers() == null) {
            return false;
        }

        return goal.getUsers().stream()
                .map(User::getId)
                .anyMatch(id -> userId == id);
    }

    private boolean isUserGoalParticipant(long userId, Goal goal) {
        if (goal.getMentor() != null && goal.getMentor().getId() == userId) {
            return true;
        }

        return goalContainsUser(goal, userId);
    }
}
