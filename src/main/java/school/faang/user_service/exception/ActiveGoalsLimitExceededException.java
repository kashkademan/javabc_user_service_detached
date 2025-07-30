package school.faang.user_service.exception;

public class ActiveGoalsLimitExceededException extends RuntimeException {
    public ActiveGoalsLimitExceededException(String message) {
        super(message);
    }
}
