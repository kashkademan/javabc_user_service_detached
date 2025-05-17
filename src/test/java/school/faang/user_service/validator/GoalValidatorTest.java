package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.goal.CountActiveGoalMoreMaxException;
import school.faang.user_service.exception.goal.GoalAlreadyCompletedException;
import school.faang.user_service.validator.goal.GoalValidator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class GoalValidatorTest {
    @InjectMocks
    private GoalValidator goalValidator;
    private long userId;
    private Goal goal;

    @BeforeEach
    public void setUp() {
        userId = 1L;

        goal = new Goal();
        goal.setId(3L);
    }

    @Test
    public void testCheckCountGoalForUser_successfully() {
        int countActiveGoalForUser = 3;

        assertDoesNotThrow(() -> goalValidator.checkCountGoalForUser(userId, countActiveGoalForUser));
    }

    @Test
    public void testCheckCountGoalForUser_goalMoreMax() {
        int countActiveGoalForUser = 4;

        assertThrows(CountActiveGoalMoreMaxException.class,
                () -> goalValidator.checkCountGoalForUser(userId, countActiveGoalForUser));
    }

    @Test
    public void testCheckGoalIsCompleted_successfully() {
        goal.setStatus(GoalStatus.ACTIVE);

        assertDoesNotThrow(() -> goalValidator.checkGoalIsCompleted(goal));
    }

    @Test
    public void testCheckGoalIsCompleted_goalAlreadyCompleted() {
        goal.setStatus(GoalStatus.COMPLETED);

        assertThrows(GoalAlreadyCompletedException.class,
                () -> goalValidator.checkGoalIsCompleted(goal));
    }
}
