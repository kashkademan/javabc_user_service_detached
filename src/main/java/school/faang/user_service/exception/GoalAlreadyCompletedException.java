package school.faang.user_service.exception;

public class GoalAlreadyCompletedException extends RuntimeException {
    public GoalAlreadyCompletedException(Long goalId, String goalTitle) {
        super(String.format("Goal '%s' (id=%d) is already completed", goalTitle, goalId));
    }
}
