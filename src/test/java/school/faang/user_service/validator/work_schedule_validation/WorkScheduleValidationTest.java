package school.faang.user_service.validator.work_schedule_validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.WorkSchedule;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class WorkScheduleValidationTest {

    @Test
    public void testAddWorkScheduleWhenStartWorkAfterStartLunch(){
        WorkSchedule workSchedule = new WorkSchedule(1L, LocalTime.of(12, 0),
                LocalTime.of(19, 0), LocalTime.of(10, 0), LocalTime.of(11, 0),
                "Europe/Berlin", new User());

        LocalTime startWork = workSchedule.getStartTime();
        LocalTime startLunch = workSchedule.getStartLunch();

        assertTrue(startWork.isAfter(startLunch) || startWork.equals(startLunch));
    }

    @Test
    public void testAddWorkScheduleWhenStartWorkAfterEndWork(){
        WorkSchedule workSchedule = new WorkSchedule(1L, LocalTime.of(19, 0),
                LocalTime.of(12, 0), LocalTime.of(10, 0), LocalTime.of(11, 0),
                "Europe/Berlin", new User());

        LocalTime startWork = workSchedule.getStartTime();
        LocalTime endWork = workSchedule.getEndTime();

        assertTrue(startWork.isAfter(endWork) || startWork.equals(endWork));
    }
    @Test
    public void testAddWorkScheduleWhenStartLunchAfterEndLunch(){
        WorkSchedule workSchedule = new WorkSchedule(1L, LocalTime.of(10, 0),
                LocalTime.of(19, 0), LocalTime.of(12, 0), LocalTime.of(11, 30),
                "Europe/Berlin", new User());

        LocalTime startLunch = workSchedule.getStartLunch();
        LocalTime endLunch = workSchedule.getEndLunch();

        assertTrue(startLunch.isAfter(endLunch) || startLunch.equals(endLunch));
    }

    @Test
    public void testAddWorkScheduleWhenEndLunchAfterEndWork(){
        WorkSchedule workSchedule = new WorkSchedule(1L, LocalTime.of(10, 0),
                LocalTime.of(17, 0), LocalTime.of(12, 0), LocalTime.of(18, 0),
                "Europe/Berlin", new User());

        LocalTime endLunch = workSchedule.getEndLunch();
        LocalTime endWork = workSchedule.getEndTime();

        assertTrue(endLunch.isAfter(endWork) || endLunch.equals(endWork));
    }

    @Test
    public void testAddWorkScheduleWhenStartWorkAfterEndLunch(){
        WorkSchedule workSchedule = new WorkSchedule(1L, LocalTime.of(10, 0),
                LocalTime.of(17, 0), LocalTime.of(10, 0), LocalTime.of(9, 0),
                "Europe/Berlin", new User());

        LocalTime startWork = workSchedule.getStartTime();
        LocalTime endLunch = workSchedule.getEndLunch();

        assertTrue(startWork.isAfter(endLunch) || startWork.equals(endLunch));
    }

    @Test
    public void testAddWorkScheduleWhenStartLunchAfterEndWork(){
        WorkSchedule workSchedule = new WorkSchedule(1L, LocalTime.of(10, 0),
                LocalTime.of(17, 0), LocalTime.of(17, 0), LocalTime.of(11, 0),
                "Europe/Berlin", new User());

        LocalTime endWork = workSchedule.getEndTime();
        LocalTime startLunch = workSchedule.getStartLunch();

        assertTrue(startLunch.isAfter(endWork) || startLunch.equals(endWork));
    }
}
