package school.faang.user_service.exception.goal;

public class UpdateComleteGoalException extends RuntimeException {

    public UpdateComleteGoalException(long goalId) {
        super("Goal with id %d is complete. Update not allowed.".formatted(goalId));
    }
}