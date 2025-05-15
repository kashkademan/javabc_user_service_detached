package school.faang.user_service.exception.goal;

public class CountActiveGoalMoreMaxException extends RuntimeException {
    public CountActiveGoalMoreMaxException(int maxGoal) {
        super(String.format("Count active goal more max, max goal %d", maxGoal));
    }
}
