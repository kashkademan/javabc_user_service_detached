package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.CreateGoalRequestDto;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.GoalFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.*;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
public class GoalService {
    private final int MAX_ACTIVE_GOALS = 3;
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final List<GoalFilter> filters;

    private final GoalMapper goalMapper;

    @Transactional
    public GoalDto createGoal(CreateGoalRequestDto request) {
        if (goalRepository.countActiveGoalsPerUser(request.userId()) >= MAX_ACTIVE_GOALS) {
            throw new IllegalArgumentException("You can't have more than 3 active goals!");
        }

        for (Long skillId : request.skillIds()) {
            if (!skillRepository.existsById(skillId)) {
                throw new IllegalArgumentException("Skill with id = " + skillId + " doesn't exist!");
            }
        }

        Goal savedGoal = goalRepository.create(request.title(), request.description(), request.parentId() != null ? request.parentId() : null);

        for (Long skill : request.skillIds()) {
            goalRepository.addSkillToGoal(skill, savedGoal.getId());
        }

        return goalMapper.toDto(savedGoal);
    }

    @Transactional
    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
       Goal existingGoal = getGoalOrThrow(goalId);

        if (existingGoal.getStatus() == GoalStatus.COMPLETED) {
            throw new IllegalArgumentException("You can't update a completed goal");
        }

        for (Long skillId : goalDto.skillIds()) {
            if (!skillRepository.existsById(skillId)) {
                throw new IllegalArgumentException("Skill doesn't exist!");
            }
        }

        goalMapper.update(existingGoal, goalDto);

        goalRepository.removeSkillsFromGoal(goalId);
        for (Long skill : goalDto.skillIds()) {
            goalRepository.addSkillToGoal(skill, goalId);
        }

        goalRepository.save(existingGoal);

        if (goalDto.status() == GoalStatus.COMPLETED) {
            List<User> users = goalRepository.findUsersByGoalId(goalId);
            for (User user : users) {
                for (Long skill : goalDto.skillIds()) {
                    skillRepository.assignSkillToUser(skill, user.getId());
                }
            }
        }

        return goalMapper.toDto(existingGoal);
    }

    @Transactional
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

    private List<GoalDto> applyFilters(Stream<Goal> goals, @Valid GoalFilterDto filterDto) {
        for (GoalFilter filter : filters) {
                goals = filter.apply(goals, filterDto);
        }
        return goals.map(goalMapper::toDto).toList();
    }

    private Goal getGoalOrThrow(long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal doesn't exist!"));
    }
}
