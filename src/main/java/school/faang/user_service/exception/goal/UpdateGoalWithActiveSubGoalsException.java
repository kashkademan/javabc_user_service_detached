package school.faang.user_service.exception.goal;

public class UpdateGoalWithActiveSubGoalsException extends RuntimeException {

    public UpdateGoalWithActiveSubGoalsException(long goalId, String activeSubGoalsId) {
        super("Goal with id %d have active sub goals [%s]. Update not allowed.".formatted(goalId, activeSubGoalsId));
    }
}