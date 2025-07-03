package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private static final String USER_HAS_NO_ACCESS = "user has no access to provided goal";
    private static final String GOAL_COMPLETED = "goal completed";
    private static final String FIELD_NOT_VALID_FORMAT = "goal's \"%s\" should be present!";
    private static final String DEADLINE_NOT_VALID_MESSAGE = "goal deadline can not to be null or date before than now";

    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final UserContext userContext;

    @Override
    public GoalDto create(CreateGoalDto createGoalDto) {
        validateCreateGoalDto(createGoalDto);
        Goal goal = goalMapper.toGoal(createGoalDto);
        goal.setStatus(GoalStatus.ACTIVE);
        if (createGoalDto.parentId() != null) {
            goal.setParent(goalRepository.getByIdOrThrow(createGoalDto.parentId()));
        }

        long userId = userContext.getUserId();
        boolean userIsMentor = false;
        if (createGoalDto.mentorId() != null) {
            userIsMentor = userId == createGoalDto.mentorId();
            goal.setMentor(userRepository.getByIdOrThrow(createGoalDto.mentorId()));
        }

        List<User> users = new ArrayList<>();
        if (createGoalDto.userIds() != null) {
            createGoalDto.userIds().forEach(id -> users.add(userRepository.getByIdOrThrow(id)));
        }
        goal.setUsers(users);
        if (!userIsMentor && !goalContainsUser(goal, userId)) {
            users.add(userRepository.getByIdOrThrow(userId));
            goal.setUsers(users);
        }

        log.info("users {}", users.stream().map(User::getId).toArray());
        goal = goalRepository.save(goal);
        return goalMapper.toGoalDto(goal);
    }

    @Override
    public GoalDto update(long goalId, UpdateGoalDto updateGoalDto) {
        validateUpdateGoalDto(updateGoalDto);
        long userId = userContext.getUserId();

        Goal goal = goalRepository.getByIdOrThrow(goalId);
        if (updateGoalDto.mentorId() != userId && goalContainsUser(goal, userId)) {
            throw new ForbiddenException(USER_HAS_NO_ACCESS);
        }

        if (GoalStatus.COMPLETED.equals(goal.getStatus())) {
            throw new IllegalStateException(GOAL_COMPLETED);
        }

        goalMapper.update(updateGoalDto, goal);
        goal = goalRepository.save(goal);
        return goalMapper.toGoalDto(goal);
    }

    @Override
    public GoalDto getById(long goalId) {
        Goal goal = goalRepository.getByIdOrThrow(goalId);
        return goalMapper.toGoalDto(goal);
    }

    private boolean goalContainsUser(Goal goal, long userId) {
        if (goal.getUsers() == null) {
            return false;
        }

        return goal.getUsers().stream()
                .map(User::getId)
                .anyMatch(id -> userId == id);
    }

    private void validateCreateGoalDto(CreateGoalDto dto) {
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

    private void validateUpdateGoalDto(UpdateGoalDto dto) {
        if (dto.title() == null || dto.title().isBlank()) {
            throw new DataValidationException(String.format(FIELD_NOT_VALID_FORMAT, "title"));
        }
        if (dto.description() == null || dto.description().isBlank()) {
            throw new DataValidationException(String.format(FIELD_NOT_VALID_FORMAT, "description"));
        }
        if (dto.deadline() == null || dto.deadline().isBefore(LocalDateTime.now())) {
            throw new DataValidationException(DEADLINE_NOT_VALID_MESSAGE);
        }
    }
}
