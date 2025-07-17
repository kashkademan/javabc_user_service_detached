package school.faang.user_service.exception;

public class GoalCompletedException extends RuntimeException {
    public GoalCompletedException(String message) {
        super(message);
    }
}
