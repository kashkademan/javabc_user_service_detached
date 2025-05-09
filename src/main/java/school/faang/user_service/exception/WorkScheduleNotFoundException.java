package school.faang.user_service.exception;

public class WorkScheduleNotFoundException extends RuntimeException{

    public WorkScheduleNotFoundException(String message) {
        super(message);
    }
}
