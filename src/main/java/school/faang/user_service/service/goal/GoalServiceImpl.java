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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private static final String USER_HAS_NO_ACCESS = "user has no access to provided goal";
    private static final String USER_HAS_TO_MANY_ACTIVE_GOALS = "user has too many ACTIVE goals";
    private static final String GOAL_COMPLETED = "goal completed";
    private static final String FIELD_NOT_VALID_FORMAT = "goal's \"%s\" should be present!";
    private static final String DEADLINE_NOT_VALID_MESSAGE = "goal deadline can not to be null or date before than now";

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final UserContext userContext;
    private final FilterService<Goal, GoalFilterDto> filterService;

    @Override
    @Transactional
    public GoalDto create(GoalCreateDto goalCreateDto) {
        validateCreateGoalDto(goalCreateDto);
        Goal goal = goalMapper.toGoal(goalCreateDto);
        goal.setStatus(GoalStatus.ACTIVE);
        if (goalCreateDto.parentId() != null) {
            goal.setParent(goalRepository.getByIdOrThrow(goalCreateDto.parentId()));
        }

        long userId = userContext.getUserId();
        boolean userIsMentor = false;
        if (goalCreateDto.mentorId() != null) {
            userIsMentor = userId == goalCreateDto.mentorId();
            goal.setMentor(userRepository.getByIdOrThrow(goalCreateDto.mentorId()));
        }

        List<User> users = new ArrayList<>();
        if (goalCreateDto.userIds() != null) {
            goalCreateDto.userIds().forEach(id -> users.add(getUserByIdOrThrow(id)));
        }
        goal.setUsers(users);
        if (!userIsMentor && !goalContainsUser(goal, userId)) {
            users.add(getUserByIdOrThrow(userId));
            goal.setUsers(users);
        }

        log.info("users {}", users.stream().map(User::getId).toArray());
        goal = goalRepository.save(goal);
        return goalMapper.toGoalDto(goal);
    }

    @Override
    @Transactional
    public GoalDto update(long goalId, GoalUpdateDto goalUpdateDto) {
        validateUpdateGoalDto(goalUpdateDto);
        long userId = userContext.getUserId();

        Goal goal = goalRepository.getByIdOrThrow(goalId);
        if (!isUserTaskParticipant(userId, goal)) {
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
    public GoalDto getById(long goalId) {
        long userId = userContext.getUserId();
        Goal goal = goalRepository.getByIdOrThrow(goalId);
        if (!isUserTaskParticipant(userId, goal)) {
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
        if (!isUserTaskParticipant(userId, goal)) {
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
    public List<GoalDto> getByFilters(GoalFilterDto filterDto) {
        userContext.getUserId();
        List<Goal> goals = goalRepository.findAll();
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

    private void validateCreateGoalDto(GoalCreateDto dto) {
        if (dto.title() == null || dto.title().isBlank()) {
            throw new DataValidationException(String.format(FIELD_NOT_VALID_FORMAT, "title"));
        }
        if (dto.description() == null || dto.description().isBlank()) {
            throw new DataValidationException(String.format(FIELD_NOT_VALID_FORMAT, "description"));
        }
        if (dto.deadline() == null || dto.deadline().isBefore(LocalDateTime.now())) {
            throw new DataValidationException(DEADLINE_NOT_VALID_MESSAGE);
        }
        if (dto.userIds() == null || dto.userIds().isEmpty()) {
            throw new DataValidationException(String.format(FIELD_NOT_VALID_FORMAT, "userIds"));
        }
    }

    private void validateUpdateGoalDto(GoalUpdateDto dto) {
        if (dto.title() == null || dto.title().isBlank()) {
            throw new DataValidationException(String.format(FIELD_NOT_VALID_FORMAT, "title"));
        }
        if (dto.description() == null || dto.description().isBlank()) {
            throw new DataValidationException(String.format(FIELD_NOT_VALID_FORMAT, "description"));
        }
        if (dto.deadline() == null || dto.deadline().isBefore(LocalDateTime.now())) {
            throw new DataValidationException(DEADLINE_NOT_VALID_MESSAGE);
        }
        if (dto.status() == null) {
            throw new DataValidationException(String.format(FIELD_NOT_VALID_FORMAT, "status"));
        }
    }

    private User getUserByIdOrThrow(long userId) {
        User user = userRepository.getByIdOrThrow(userId);
        List<Goal> goals = user.getGoals();
        if (goals == null || goals.isEmpty()) {
            return user;
        }
        long size = goals.stream()
                .filter(g -> g.getStatus().equals(GoalStatus.ACTIVE))
                .count();
        if (size > 1) {
            throw new DataValidationException(USER_HAS_TO_MANY_ACTIVE_GOALS);
        }
        return user;
    }

    private boolean isUserTaskParticipant(long userId, Goal goal) {
        if (goal.getMentor() != null && goal.getMentor().getId() == userId) {
            return true;
        }

        return goalContainsUser(goal, userId);
    }
}
