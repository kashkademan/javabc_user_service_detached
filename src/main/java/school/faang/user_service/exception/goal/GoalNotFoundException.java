package school.faang.user_service.exception.goal;

import java.util.NoSuchElementException;

public class GoalNotFoundException extends NoSuchElementException {
    public GoalNotFoundException(long goalId) {
        super(String.format("Goal with id %d not found", goalId));
    }
}
