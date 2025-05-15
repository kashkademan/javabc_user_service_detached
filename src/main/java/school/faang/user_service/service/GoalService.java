package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.filter.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.goal.GoalNotExistException;
import school.faang.user_service.exception.goal.UpdateComleteGoalException;
import school.faang.user_service.exception.goal.UserNotGoalOwnerException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.goal.GoalValidator;
import school.faang.user_service.validator.goal.SkillValidator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static school.faang.user_service.util.EntityUtil.setIfNotNull;
import static school.faang.user_service.util.EntityUtil.setIfTrue;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final UserContext userContext;
    private final GoalRepository goalRepository;
    private final GoalValidator goalValidator;

    private final SkillService skillService;
    private final SkillValidator skillValidator;

    private final List<GoalFilter> filters;

    @Transactional
    public Goal createGoal(Goal newGoalData, List<Long> skillsId, Long parentId) {
        long userId = userContext.getUserId();
        goalValidator.validateMaxActiveGoalLimitPerUser(userId);

        newGoalData.setStatus(GoalStatus.ACTIVE);
        setIfNotNull(parentId, id -> newGoalData.setParent(getGoalById(id)));
        setIfTrue(skillsId,
                ids -> !ids.isEmpty(),
                ids -> newGoalData.setSkillsToAchieve(skillService.findSkillsById(skillsId)));

        Goal newGoal = goalRepository.save(newGoalData);
        goalRepository.assignGoalToUser(userId, newGoal.getId());

        return newGoal;
    }

    @Transactional
    public Goal update(long goalId, Goal newGoalData, List<Long> skillsId) {
        Goal dbGoal = getGoalById(goalId);

        if(dbGoal.getStatus() == GoalStatus.COMPLETED) {
            throw new UpdateComleteGoalException(goalId);
        }

        skillValidator.validateExistingSkills(skillsId);

        updateEntity(dbGoal, newGoalData);

        if (isSkillsListUpdated(dbGoal, skillsId)) {
            skillService.removeSkillForGoal(goalId);
            dbGoal.setSkillsToAchieve(skillService.findSkillsById(skillsId));
        }

        if (dbGoal.getStatus() == GoalStatus.COMPLETED) {
            List<Long> involvedUsersId = goalRepository.findUsersByGoalId(dbGoal.getId());

            goalValidator.validateAllSubGoalsCompleted(goalId, goalRepository.findByParent(goalId));

            involvedUsersId.forEach(userId ->
                    skillService.assignSkillsToUser(userId, dbGoal.getSkillsToAchieve()));
        }

        goalRepository.save(dbGoal);
        return dbGoal;
    }

    @Transactional
    public void delete(long goalId) {
        Goal goal = getGoalById(goalId);

        boolean userNotOwner = goal.getUsers()
                .stream()
                .noneMatch(user -> Objects.equals(user.getId(), userContext.getUserId()));

        if (userNotOwner) {
            throw new UserNotGoalOwnerException(userContext.getUserId(), goalId);
        }

        goalRepository.removeGoalFromUser(userContext.getUserId(), goalId);

        if (goalRepository.findUsersByGoalId(goalId).isEmpty()) {
            goalRepository.findByParent(goalId).forEach(goalRepository::delete);
            goalRepository.delete(goal);
        }
    }

    @Transactional(readOnly = true)
    public Goal getGoalById(long goalId) {
        return goalRepository.findById(goalId).orElseThrow(() -> new GoalNotExistException(goalId));
    }

    @Transactional(readOnly = true)
    public List<Goal> getGoalsByFilter(GoalFilterDto goalFilterDto) {
        return filterGoals(goalRepository.findAll().stream(), goalFilterDto).toList();
    }

    @Transactional(readOnly = true)
    public List<Goal> getSubGoalsByFilter(long parentId, GoalFilterDto goalFilterDto) {
        return filterGoals(goalRepository.findByParent(parentId), goalFilterDto).toList();
    }

    private Stream<Goal> filterGoals(Stream<Goal> goalStream, GoalFilterDto goalFilterDto) {
        return filters.stream()
                .filter(filter -> filter.isApplicable(goalFilterDto))
                .reduce(goalStream,
                        (currentStream, filter) -> filter.apply(currentStream, goalFilterDto),
                        Stream::concat);
    }

    private void updateEntity(Goal targetGoal, Goal newGoalData) {
        setIfNotNull(newGoalData.getTitle(), targetGoal::setTitle);
        setIfNotNull(newGoalData.getDescription(), targetGoal::setDescription);
        setIfNotNull(newGoalData.getStatus(), targetGoal::setStatus);
        setIfNotNull(newGoalData.getDeadline(), targetGoal::setDeadline);
    }

    private boolean isSkillsListUpdated(Goal targetGoal, List<Long> skillsId) {
        return targetGoal.getSkillsToAchieve().size() != skillsId.size()
                || !targetGoal.getSkillsToAchieve()
                .stream()
                .map(Skill::getId)
                .allMatch(skillsId::contains);
    }
}