package school.faang.user_service.exception.work_schedule;

public class WorkScheduleNotFoundException extends RuntimeException{

    public WorkScheduleNotFoundException(String message) {
        super(message);
    }
}
