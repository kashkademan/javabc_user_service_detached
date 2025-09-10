package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalCompleteEvent;
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
import school.faang.user_service.publisher.GoalEventCompletePublisher;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.filter.FilterService;

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
    private static final int MAX_NUM_POSSIBLE_ACTIVE_GOALS = 2;

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final UserContext userContext;
    private final FilterService<Goal, GoalFilterDto> filterService;
    private final GoalEventCompletePublisher publisher;

    @Override
    @Transactional
    public GoalDto create(GoalCreateDto goalCreateDto) {
        long currentUserId = userContext.getUserId();
        Goal goal = goalMapper.toGoal(goalCreateDto);
        if (goalCreateDto.parentId() != null) {
            Goal parent = goalRepository.getByIdOrThrow(goalCreateDto.parentId());
            goal.setParent(parent);
        }

        boolean userIsMentor = true;
        if (goalCreateDto.mentorId() != null) {
            userIsMentor = (currentUserId == goalCreateDto.mentorId());
//            if (!userIsMentor) {
//                throw new ForbiddenException(USER_HAS_NO_ACCESS);
//            }
            User mentor = userRepository.getByIdOrThrow(goalCreateDto.mentorId());
            goal.setMentor(mentor);
        }

        List<User> users = getUsersForGoalOrThrow(userIsMentor, goalCreateDto.userIds());
        goal.setUsers(users);
        goal = goalRepository.save(goal);
        return goalMapper.toGoalDto(goal);
    }

    private List<User> getUsersForGoalOrThrow(boolean isMentor, List<Long> userIds) {
        long currentUserId = userContext.getUserId();
        User currentUser = userRepository.getByIdOrThrow(currentUserId);
        List<User> users = List.of(currentUser);

        if (isMentor) {
            users = currentUser.getMentees();
            if (users == null || users.isEmpty()) {
                throw new ForbiddenException(MENTOR_HAS_NO_MENTEES);
            }
        }

        List<User> usersForGoal = new ArrayList<>();
        for (Long id : userIds) {
            User user = users.stream()
                    .filter(u -> u.getId().equals(id))
                    .findAny()
                    .orElseThrow(() -> new ForbiddenException(USER_HAS_NO_ACCESS_TO_CREATE));

            if (user.getGoals() == null || user.getGoals().isEmpty()) {
                usersForGoal.add(user);
                continue;
            }
            validateUserGoalsCount(user);
            usersForGoal.add(user);
        }
        return usersForGoal;
    }

    private void validateUserGoalsCount(User user) {
        long activeGoalsCount = user.getGoals().stream()
                .filter(g -> g.getStatus().equals(GoalStatus.ACTIVE))
                .count();
        if (activeGoalsCount >= MAX_NUM_POSSIBLE_ACTIVE_GOALS) {
            throw new DataValidationException(USER_HAS_TO_MANY_ACTIVE_GOALS);
        }
    }

    @Override
    @Transactional
    public GoalDto update(long goalId, GoalUpdateDto goalUpdateDto) {
        long currentUserId = userContext.getUserId();

        Goal goal = goalRepository.getByIdOrThrow(goalId);
        if (!isUserGoalParticipant(currentUserId, goal)) {
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
    public void completeGoal(long goalId) {
        long currentUserId = userContext.getUserId();

        Goal goal = goalRepository.getByIdOrThrow(goalId);

        if (!isUserGoalParticipant(currentUserId, goal)) {
            throw new ForbiddenException(USER_HAS_NO_ACCESS);
        }

        if (GoalStatus.COMPLETED.equals(goal.getStatus())) {
            throw new IllegalStateException(GOAL_COMPLETED);
        }

        goal.setStatus(GoalStatus.COMPLETED);
        goalRepository.save(goal);

        publisher.publish(new GoalCompleteEvent(goalId, currentUserId));
    }

    @Override
    @Transactional
    public GoalDto getById(long goalId) {
        long currentUserId = userContext.getUserId();
        Goal goal = goalRepository.getByIdOrThrow(goalId);
        if (!isUserGoalParticipant(currentUserId, goal)) {
            throw new ForbiddenException(USER_HAS_NO_ACCESS);
        }
        return goalMapper.toGoalDto(goal);
    }

    @Override
    @Transactional
    public void delete(long goalId) {
        long currentUserId = userContext.getUserId();
        Goal goal = goalRepository.getByIdOrThrow(goalId);
        boolean hasMentor = goal.getMentor() != null;
        if (!isUserGoalParticipant(currentUserId, goal)) {
            throw new ForbiddenException(USER_HAS_NO_ACCESS);
        }

        long usersCount = goal.getUsers() == null ? 0 :
                goal.getUsers().stream()
                        .map(User::getId)
                        .collect(Collectors.toSet())
                        .size();

        if (hasMentor && goal.getMentor().getId() == currentUserId || usersCount == 1) {
            goalRepository.deleteById(goalId);
            return;
        }

        goalRepository.deleteUserFromGoal(currentUserId, goalId);
    }

    @Override
    @Transactional
    public List<GoalDto> getByFilters(GoalFilterDto filterDto) {
        long currentUserId = userContext.getUserId();
        List<Goal> goals = goalRepository.findGoalsByUserId(currentUserId).toList();
        goals = filterService.getFilteredList(goals, filterDto);
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
