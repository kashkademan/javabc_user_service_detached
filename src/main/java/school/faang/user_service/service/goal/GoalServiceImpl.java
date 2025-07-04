package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ActiveGoalsLimitExceededException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.exception.GoalCompletedException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "goal")
public class GoalServiceImpl implements GoalService {
    @Value("${min-participants-count:1}")
    private int minParticipantsCount;
    @Value("${active-goals-limit:3}")
    private int activeGoalsLimit;

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalMapper mapper;
    private final UserContext userContext;

    @Override
    public GoalDto create(CreateGoalDto createGoalDto) {
        long currentId = userContext.getUserId();
        Goal goal = mapper.toGoal(createGoalDto);
        Long mentorId = createGoalDto.mentorId();
        List<User> users = userRepository.findAllById(createGoalDto.userIds());

        if (!isMentor(mentorId, currentId) && !isIndependentUser(mentorId, currentId, users)) {
            throw new ForbiddenException("User " + currentId + " doesn't have authorities to create goal");
        }
        if (mentorId != null) {
            User mentor = userRepository.getByIdOrThrow(mentorId);
            goal.setMentor(mentor);
        }

        Long parentGoalId = createGoalDto.parentId();
        if (parentGoalId != null) {
            Goal parentGoal = goalRepository.getByIdOrThrow(parentGoalId);
            goal.setParent(parentGoal);
        }

        checkOverActiveGoalLimitFor(createGoalDto.userIds());
        goal.setUsers(users);
        goal.setStatus(GoalStatus.ACTIVE);

        goal = goalRepository.save(goal);

        log.info("Goal {} created by user {}", goal.getId(), currentId);
        return mapper.toGoalDto(goal);
    }

    @Override
    public GoalDto update(long goalId, UpdateGoalDto updateGoalDto) {
        long currentId = userContext.getUserId();
        Goal goal = goalRepository.getGoalWithUsersByIdOrThrow(goalId);

        if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new GoalCompletedException(
                    "Goal " + goal.getId() + " can't be updated because - status is " + goal.getStatus());
        }

        Long mentorId = getMentorId(goal);

        if (!canUpdateGoal(mentorId, currentId, goal.getUsers(), updateGoalDto.status())) {
            throw new ForbiddenException(
                    "User " + currentId + " doesn't have authorities to update goal " + goal.getId());
        }

        mapper.update(updateGoalDto, goal);

        if (isMentor(mentorId, currentId) && needMentorUpdate(mentorId, updateGoalDto.mentorId())) {
            User mentor = userRepository.getByIdOrThrow(updateGoalDto.mentorId());
            goal.setMentor(mentor);
        }

        goal = goalRepository.save(goal);

        log.info("Goal {} updated by user {}", goal.getId(), currentId);
        return mapper.toGoalDto(goal);
    }

    @Override
    public void delete(long goalId) {
        long currentId = userContext.getUserId();
        Goal goal = goalRepository.getGoalWithUsersByIdOrThrow(goalId);
        Long mentorId = getMentorId(goal);

        if (!isMentor(mentorId, currentId) && !isParticipant(goal.getUsers(), currentId)) {
            throw new ForbiddenException(
                    "User " + currentId + " doesn't have authorities to delete goal " + goal.getId());
        }

        if (canDeleteGoal(mentorId, currentId, goal.getUsers())) {
            goalRepository.deleteById(goalId);
            log.info("Goal {} was deleted by user {}", goal.getId(), currentId);
        } else {
            goalRepository.deleteUserFromGoal(currentId, goalId);
            log.info("User {} was deleted from goal {}", currentId, goal.getId());
        }
    }

    @Override
    public List<GoalDto> getByFilters(GoalFilterDto filters) {
        List<Goal> goalsByFilters = goalRepository.findGoalsByFilters(filters.titleContains(),
                                                                      filters.descriptionContains(),
                                                                      filters.mentorId(),
                                                                      filters.status());

        return goalsByFilters.stream()
                .map(mapper::toGoalDto)
                .toList();
    }

    private void checkOverActiveGoalLimitFor(List<Long> userIds) {
        List<Long> usersWithExceededLimit =
                goalRepository.findUserIdsOverActiveGoalLimit(userIds, GoalStatus.ACTIVE.ordinal(), activeGoalsLimit);

        if (!usersWithExceededLimit.isEmpty()) {
            throw new ActiveGoalsLimitExceededException(
                    "Active goals limit exceeded for users: " + usersWithExceededLimit);
        }
    }

    private boolean isMentor(Long mentorId, long currentId) {
        return mentorId != null && mentorId.equals(currentId);
    }

    private boolean isParticipant(List<User> users, long currentId) {
        return users != null
               && users.stream()
                       .anyMatch(user -> user.getId().equals(currentId));
    }

    private boolean isIndependentUser(Long mentorId, long currentId, List<User> users) {
        return mentorId == null && users != null && users.size() == 1
               && users.stream()
                       .anyMatch(user -> user.getId().equals(currentId));
    }

    private Long getMentorId(Goal goal) {
        return Optional.ofNullable(goal.getMentor())
                .map(User::getId)
                .orElse(null);
    }

    private boolean needMentorUpdate(Long currentMentorId, Long newMentorId) {
        return currentMentorId != null && newMentorId != null && !currentMentorId.equals(newMentorId);
    }

    private boolean canUpdateGoal(Long mentorId, long currentId, List<User> users, GoalStatus status) {
        return (isMentor(mentorId, currentId) || isIndependentUser(mentorId, currentId, users))
               || (isParticipant(users, currentId) && !status.equals(GoalStatus.COMPLETED));
    }

    private boolean canDeleteGoal(Long mentorId, long currentId, List<User> users) {
        return isMentor(mentorId, currentId) || (users != null && users.size() <= minParticipantsCount);
    }
}
