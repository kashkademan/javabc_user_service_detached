package school.faang.user_service.service.goal;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final UserContext userContext;

    @Transactional
    public Goal createGoal(GoalDto goalDto) {
        validateUserGoals();
        validateSkills(goalDto.getSkillsToAchie());

        Goal savedGoal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParent());
        goalDto.getSkillsToAchie().stream()
            .filter(skillRepository::existsById)
            .forEach(skillId -> goalRepository.addSkillToGoal(goalDto.getId(), skillId));

        return savedGoal;
    }

    public void updateGoal() {

    }

    public void deleteGoal() {

    }

    public void findSubtasksByGoalId() {

    }

    public void findGoalsByUserId() {

    }

    private void validateUserGoals() {
        int activeGoalsCount = goalRepository.countActiveGoalsPerUser(userContext.getUserId());
        if (activeGoalsCount > 2) {
            throw new IllegalArgumentException("User has reached the maximum number of goals.");
        }
    }

    private void validateSkills(List<Long> skillIds) {
        if (skillRepository.countExisting(skillIds) == 0) {
            throw new IllegalArgumentException("No skills registered. Cannot create goal without skills.");
        }
    }
}
