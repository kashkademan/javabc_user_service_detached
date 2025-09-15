package school.faang.user_service.exception;

public class GoalNotFoundException extends RuntimeException {
    public GoalNotFoundException(Long goalId) {
        super("Goal not found with id: " + goalId);
    }
}
