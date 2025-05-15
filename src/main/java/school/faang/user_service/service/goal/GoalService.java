package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalCreateRequestDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalResponseDto;
import school.faang.user_service.dto.goal.GoalUpdateRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.goal.CountActiveGoalMoreMaxException;
import school.faang.user_service.exception.goal.GoalAlreadyCompletedException;
import school.faang.user_service.exception.goal.GoalNotFoundException;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalService {
    private static final int MAX_NUM_ACTIVE_GOAL_FOR_USER = 3;
    private final GoalRepository goalRepository;
    private final UserService userService;
    private final SkillService skillService;
    private final GoalMapper goalMapper;
    private final UserContext userContext;

    @Transactional
    public GoalResponseDto createGoal(final GoalCreateRequestDto goalCreateRequestDto) {
        Goal goal = goalMapper.toGoalEntity(goalCreateRequestDto);
        long userId = userContext.getUserId();

        checkCountGoalForUser(userId);
        checkSkills(goalCreateRequestDto.getSkillIds());

        User owner = userService.getUserById(userId);
        List<User> users = new ArrayList<>();
        users.add(owner);
        goal.setUsers(users);

        Goal parentGoal = getGoalByIdOrThrow(goalCreateRequestDto.getParentId());
        goal.setParent(parentGoal);

        goal.setStatus(GoalStatus.ACTIVE);

        Goal savedGoal = goalRepository.save(goal);
        log.info("Goal with id {} has been saved", savedGoal.getId());

        return goalMapper.toGoalResponseDto(savedGoal);
    }

    @Transactional
    public GoalResponseDto updateGoal(final GoalUpdateRequestDto goalUpdateRequestDto) {
        Goal goal = getGoalByIdOrThrow(goalUpdateRequestDto.getId());

        checkGoalIsCompleted(goal);
        checkSkills(goalUpdateRequestDto.getSkillIds());

        goalMapper.update(goal, goalUpdateRequestDto);

        List<Skill> skills = goalUpdateRequestDto.getSkillIds().stream()
                .map(skillService::getSkillByIdOrThrow)
                .collect(Collectors.toList());
        goal.setSkillsToAchieve(skills);

        Goal saveGoal = goalRepository.save(goal);
        log.info("Goal with id {} has been update", saveGoal.getId());

        assignSkillsToAllUsersIfGoalCompleted(saveGoal);

        return goalMapper.toGoalResponseDto(saveGoal);
    }

    @Transactional
    public void deleteGoalById(long goalId) {
        getGoalByIdOrThrow(goalId);

        goalRepository.deleteById(goalId);
        log.info("Goal with id {} has been deleted", goalId);
    }


    @Transactional(readOnly = true)
    public List<GoalResponseDto> getSubtasksByParentGoalId(long goalParentId) {
        try (Stream<Goal> goalsStream = goalRepository.findByParent(goalParentId)) {
            return goalsStream
                    .map(goalMapper::toGoalResponseDto)
                    .toList();
        }
    }


    @Transactional(readOnly = true)
    public List<GoalResponseDto> getGoalsByUser(GoalFilterDto filter) {
        long userId = userContext.getUserId();
        GoalStatus statusFilter = filter.getCompleted() ? GoalStatus.COMPLETED : GoalStatus.ACTIVE;

        try (Stream<Goal> goalsStream = goalRepository.findGoalsByUserId(userId)) {
            return goalsStream
                    .filter(goal -> Objects.equals(goal.getTitle(), filter.getTitle()))
                    .filter(goal -> Objects.equals(goal.getStatus(), statusFilter))
                    .map(goalMapper::toGoalResponseDto)
                    .toList();
        }
    }

    @Transactional
    public Goal getGoalByIdOrThrow(long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> {
                    log.error("Goal with id {} not found", goalId);
                    return new GoalNotFoundException(goalId);
                });
    }

    private static void checkGoalIsCompleted(Goal goal) {
        if (Objects.equals(goal.getStatus(), GoalStatus.COMPLETED)) {
            String errorMsg = String.format("Trying change the completed goal with id %d", goal.getId());
            log.error(errorMsg);
            throw new GoalAlreadyCompletedException(errorMsg);
        }
    }

    private void checkCountGoalForUser(long userId) {
        int countActiveGoalForUser = goalRepository.countActiveGoalsPerUser(userId);

        log.debug("Count active goal for user with id {} {}", userId, countActiveGoalForUser);

        if (countActiveGoalForUser > MAX_NUM_ACTIVE_GOAL_FOR_USER) {
            log.error("Count active goal more max, max goal {}", MAX_NUM_ACTIVE_GOAL_FOR_USER);
            throw new CountActiveGoalMoreMaxException(MAX_NUM_ACTIVE_GOAL_FOR_USER);
        }
    }

    private void assignSkillsToAllUsersIfGoalCompleted(Goal saveGoal) {
        if (Objects.equals(saveGoal.getStatus(), GoalStatus.COMPLETED)) {
            List<Long> userIds = saveGoal.getUsers().stream()
                    .map(User::getId)
                    .toList();
            List<Long> skillIds = saveGoal.getSkillsToAchieve().stream()
                    .map(Skill::getId)
                    .toList();
            skillService.assignSkillsToUsers(skillIds, userIds);
        }
    }

    private void checkSkills(List<Long> skillIds) {
        skillIds.forEach(skillService::getSkillByIdOrThrow);
    }
}
