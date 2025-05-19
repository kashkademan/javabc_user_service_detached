package school.faang.user_service.validation.work_schedule_validation;

import school.faang.user_service.entity.WorkSchedule;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalTime;

public class WorkScheduleValidation {
    public static void checkCorrectedWorkSchedule(WorkSchedule workSchedule) {
        LocalTime startTime = workSchedule.getStartTime();
        LocalTime startLunch = workSchedule.getStartLunch();
        LocalTime endLunch = workSchedule.getEndLunch();
        LocalTime endTime = workSchedule.getEndTime();

        boolean isStartBeforeLaunch = startTime.isBefore(startLunch);
        boolean isLaunchBeforeLaunchEnd = startLunch.isBefore(endLunch);
        boolean isLaunchTimeBeforeEnd = endLunch.isBefore(endTime);

        if (!(isStartBeforeLaunch && isLaunchBeforeLaunchEnd && isLaunchTimeBeforeEnd)) {
            throw new DataValidationException("Не верно введено время работы");
        }
    }
}
