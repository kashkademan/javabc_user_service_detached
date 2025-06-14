package school.faang.user_service.exception.goal;

import jakarta.persistence.EntityNotFoundException;

public class GoalNotFoundException extends EntityNotFoundException {
    public GoalNotFoundException(long goalId) {
        super(String.format("Goal with id %d not found", goalId));
    }
}
