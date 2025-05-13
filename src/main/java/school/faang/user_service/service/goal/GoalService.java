package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.filter.goal.GoalFilter;

import java.util.List;
import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
@Transactional
public class GoalService {
    private final int MAX_ACTIVE_GOALS = 3;
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final List<GoalFilter> filters;
    private final GoalMapper goalMapper;

    public void createGoal(Long userId, Goal goal) {
        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_ACTIVE_GOALS) {
            throw new IllegalArgumentException("You can't have more than 3 active goals!");
        }

        goal.getSkillsToAchieve().forEach(skill -> {
                    if (skill.getId() == 0 || !skillRepository.existsById(skill.getId())) {
                        throw new IllegalArgumentException("Skill doesn't exist!");
                    }});

        Goal savedGoal = goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent() != null ? goal.getParent().getId() : null);

        for (Skill skill : goal.getSkillsToAchieve()) {
            goalRepository.addSkillToGoal(skill.getId(), savedGoal.getId());
        }
    }

    public void updateGoal(Long goalId, Goal goal) {
       Goal existingGoal = getGoalOrThrow(goalId);

        if (existingGoal.getStatus() == GoalStatus.COMPLETED) {
            throw new IllegalArgumentException("You can't update a completed goal");
        }

        goal.getSkillsToAchieve().forEach(skill -> {
            if (skill.getId() == 0 || !skillRepository.existsById(skill.getId())) {
                throw new IllegalArgumentException("Skill doesn't exist!");
            }
        });

        existingGoal.setTitle(goal.getTitle());
        existingGoal.setDescription(goal.getDescription());
        existingGoal.setStatus(goal.getStatus());

        goalRepository.removeSkillsFromGoal(goalId);
        for (Skill skill : goal.getSkillsToAchieve()) {
            goalRepository.addSkillToGoal(skill.getId(), goalId);
        }

        goalRepository.save(existingGoal);

        if (goal.getStatus() == GoalStatus.COMPLETED) {
            List<User> users = goalRepository.findUsersByGoalId(goalId);

            for (User user : users) {
                for (Skill skill : goal.getSkillsToAchieve()) {
                    skillRepository.assignSkillToUser(skill.getId(), user.getId());
                }
            }
        }
    }

    public void deleteGoal(long goalId) {
        Goal goal = getGoalOrThrow(goalId);

        if (goalRepository.findByParent(goalId).findAny().isPresent()) {
            throw new IllegalStateException("Cannot delete goal with subgoals");
        }

        goalRepository.removeSkillsFromGoal(goalId);

        goalRepository.delete(goal);
    }

    public List<GoalDto> findSubtasksByGoalId(Long goalId, GoalFilterDto goalFilterDto) {
        return applyFilters(goalRepository.findByParent(goalId), goalFilterDto);
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto goalFilterDto) {
        return applyFilters(goalRepository.findGoalsByUserId(userId), goalFilterDto);
    }

    private List<GoalDto> applyFilters(Stream<Goal> goals, GoalFilterDto filterDto) {
        for (GoalFilter filter : filters) {
            if (filter.isApplicable(filterDto)) {
                goals = filter.apply(goals, filterDto);
            }
        }
        return goals.map(goalMapper::toDto).toList();
    }

    private Goal getGoalOrThrow(long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal doesn't exist!"));
    }
}
