package school.faang.user_service.exception.goal;

public class UserNotGoalOwnerException extends RuntimeException {

    public UserNotGoalOwnerException(long userId, long goalId) {
        super("User with id %d not the owner of goal with id %d".formatted(userId, goalId));
    }
}