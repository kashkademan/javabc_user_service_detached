package school.faang.user_service.validator.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.goal.MaxActiveGoalPerUserException;
import school.faang.user_service.exception.goal.UpdateGoalWithActiveSubGoalsException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validation.goal.GoalProperties;
import school.faang.user_service.validation.goal.GoalValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalValidationTest {

    private static final Long USER_ID = 1L;
    private static final Long GOAL_ID = 1L;
    private static final int GOAL_LIMIT = 3;

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private GoalProperties goalProperties;
    @InjectMocks
    private GoalValidator validator;

    @Test
    void testUserGoalLimitExceededValidation() {
        when(goalProperties.getMaxLimit()).thenReturn(GOAL_LIMIT);
        when(goalRepository.countActiveGoalsPerUser(USER_ID)).thenReturn(GOAL_LIMIT);

        assertThrows(MaxActiveGoalPerUserException.class, () -> validator.validateMaxActiveGoalLimitPerUser(USER_ID));
    }

    @Test
    void testUserGoalLimitNotExceededValidation() {
        when(goalProperties.getMaxLimit()).thenReturn(GOAL_LIMIT);
        when(goalRepository.countActiveGoalsPerUser(USER_ID)).thenReturn(GOAL_LIMIT - 1);

        assertDoesNotThrow(() -> validator.validateMaxActiveGoalLimitPerUser(USER_ID));
    }

    @Test
    void testCompleteGoalWithActiveSubGoalsValidation() {
        List<Goal> notAllCompletedSubGoals = List.of(
                Goal.builder().status(GoalStatus.COMPLETED).build(),
                Goal.builder().status(GoalStatus.ACTIVE).build(),
                Goal.builder().status(GoalStatus.COMPLETED).build()
        );

        assertThrows(
                UpdateGoalWithActiveSubGoalsException.class,
                () -> validator.validateAllSubGoalsCompleted(GOAL_ID, notAllCompletedSubGoals.stream())
        );
    }

    @Test
    void testCompleteGoalWithCompletedSubGoalsValidation() {
        List<Goal> notAllCompletedSubGoals = List.of(
                Goal.builder().id(2L).status(GoalStatus.COMPLETED).build(),
                Goal.builder().id(3L).status(GoalStatus.COMPLETED).build(),
                Goal.builder().id(4L).status(GoalStatus.COMPLETED).build()
        );

        assertDoesNotThrow(() -> validator.validateAllSubGoalsCompleted(GOAL_ID, notAllCompletedSubGoals.stream()));
    }
}