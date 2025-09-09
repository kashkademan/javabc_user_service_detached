package school.faang.user_service.exception;

public class GoalNotAssignedToUserException extends RuntimeException {
    public GoalNotAssignedToUserException(Long userId, Long goalId) {
        super("Goal with Id " + goalId + " does not belong to user with Id " + userId);
    }
}
