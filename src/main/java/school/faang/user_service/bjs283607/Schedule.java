package school.faang.user_service.bjs283607;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import school.faang.user_service.exception.EntityNotFoundException;

import java.util.HashMap;
import java.util.Map;

@ToString
@EqualsAndHashCode
public class Schedule {
    private Map<String, Event> schedule = new HashMap<>();
    private String name;

    public Schedule(String name) {
        this.name = name;
    }

    public void addSchedule(String day, String startHour, String startMinutes) {
        schedule.put(day, new Event(startHour, startMinutes));
    }

    public void updateSchedule(String day, String startHour, String startMinutes) {
        if (schedule.containsKey(day)) {
            schedule.put(day, new Event(startHour, startMinutes));
        } else {
            throw new EntityNotFoundException("Записи не найдено!");
        }
    }
}
