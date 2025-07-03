package school.faang.user_service.service.goal;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final UserContext userContext;

    public void createGoal(GoalDto goalDto) {
        User currentUser = userRepository.findById(userContext.getUserId()).orElseThrow(EntityNotFoundException::new);
        if (currentUser.getGoals().size() > 2) {
            throw new IllegalArgumentException("User has reached the maximum number of goals.");
        }
        Goal savedGoal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
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
