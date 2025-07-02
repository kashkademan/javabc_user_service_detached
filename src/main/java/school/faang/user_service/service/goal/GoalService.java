package school.faang.user_service.service.goal;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    public void createGoal() {
        Goal savedGoal = goalRepository.create("Title", "Description", 1L);
        goalRepository.addSkillToGoal(1L, savedGoal.getId());
    }

    public void updateGoal() {

    }

    public void deleteGoal() {

    }

    public void findSubtasksByGoalId() {

    }

    public void findGoalsByUserId() {

    }
}
