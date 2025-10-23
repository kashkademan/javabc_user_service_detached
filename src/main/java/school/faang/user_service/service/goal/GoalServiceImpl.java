package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;
    private final UserContext userContext;

    @Transactional
    @Override
    public GoalDto create(CreateGoalDto createGoalDto) {
        Goal goal = goalMapper.toGoal(createGoalDto);
        validatePossibleToCreateOrUpdate(goal);
        goalRepository.save(goal);
        log.info("Goal with title {} was created successfully for User with id {}!",
                createGoalDto.title(), createGoalDto.userIds());
        return goalMapper.toGoalDto(goal);
    }

    @Transactional
    @Override
    public GoalDto update(long goalId, UpdateGoalDto updateGoalDto) {
        Goal goal = goalRepository.getByIdOrThrow(goalId);
        validatePossibleToCreateOrUpdate(goal);
        goalMapper.update(updateGoalDto, goal);
        log.info("Goal with id {} was updated successfully!", goalId);
        return goalMapper.toGoalDto(goal);
    }

    @Transactional
    @Override
    public void deleteGoal(long goalId) {
        Goal goal = goalRepository.getByIdOrThrow(goalId);
        goal.getUsers().clear();
        goalRepository.save(goal);
        goalRepository.delete(goal);
    }

    @Transactional
    @Override
    public void deleteGoalFromUser(long goalId, long userId) {
        goalRepository.deleteUserFromGoal(userId, goalId);
    }

    @Transactional
    @Override
    public List<GoalDto> getByFilters(GoalFilterDto filters) {
        return goalRepository.findAll().stream()
                .filter(goal -> filters.titleContains() == null
                        || goal.getTitle().toLowerCase().contains(filters.titleContains().toLowerCase()))
                .filter(goal -> filters.descriptionContains() == null
                        || goal.getDescription().toLowerCase()
                                .contains(filters.descriptionContains().toLowerCase()))
                .filter(goal -> filters.status() == null
                        || goal.getStatus() == filters.status())
                .filter(goal -> filters.mentorId() == null
                        || goal.getMentor().getId().equals(filters.mentorId()))
                .map(goalMapper::toGoalDto)
                .toList();
    }

    private void validatePossibleToCreateOrUpdate(Goal goal) {
        boolean userNotAllowed = !ablePersonToCreateGoal(goal);
        boolean noAvailablePerformers = getAvailablePerformers(goal).isEmpty();
        boolean goalStatusCompleted = isStatusCompleted(goal);
        boolean userAllowed = canUserUpdateGoal(goal);
        if (userNotAllowed || noAvailablePerformers || goalStatusCompleted || !userAllowed) {
            log.error("User is not allowed or no available performers for goal {}", goal.getId());
            throw new ForbiddenException("You cannot create or update this goal");
        }
    }

    private boolean ablePersonToCreateGoal(Goal goal) {
        long userId = userContext.getUserId();
        boolean isMentor = goal.getMentor() != null && userId == goal.getMentor().getId();
        boolean assignForHimself = goal.getUsers() != null && goal.getUsers().stream()
                .anyMatch(user -> userId == user.getId());

        return isMentor || assignForHimself;
    }

    private List<Long> getAvailablePerformers(Goal goal) {
        List<User> users = userRepository.findAllByIdIn(goal.getUsers().stream()
                .map(User::getId)
                .toList());
        return users.stream().filter(user -> user.getGoals().size() < 2).map(User::getId).toList();
    }

    private boolean isStatusCompleted(Goal goal) {
        return goal.getStatus().equals(GoalStatus.COMPLETED);
    }

    private boolean hasGoalMentor(Goal goal) {
        return goal.getMentor() != null;
    }

    private boolean canUserUpdateGoal(Goal goal) {
        if (!hasGoalMentor(goal)) {
            return true;
        }
        return userContext.getUserId() == goal.getMentor().getId();
    }
}

