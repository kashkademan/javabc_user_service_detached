package school.faang.user_service.exception.goal;

public class GoalNotExistException extends RuntimeException {

    public GoalNotExistException(long goalId) {
        super("Goal with id %d not exist".formatted(goalId));
    }
}