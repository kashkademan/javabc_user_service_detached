package school.faang.user_service.exception.goal;

public class MaxActiveGoalPerUserException extends RuntimeException {

    public MaxActiveGoalPerUserException(Long userId, int goalLimit) {
        super("User with id %s reach max goals active limit of %d".formatted(userId, goalLimit));
    }
}