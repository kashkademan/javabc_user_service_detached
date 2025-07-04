package school.faang.user_service.bjs283607;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@ToString
@Slf4j
@EqualsAndHashCode
public class Schedule {
    private Map<String, Event> schedule = new HashMap<>();
    private String name;

    public Schedule(String name) {
        this.name = name;
    }

    public void addSchedule(String day, String starthour, String startminet) {
        schedule.put(day, new Event(starthour, startminet));
    }

    public void updateSchedule(String day, String starthour, String startminet) {
        if (schedule.containsKey(day)) {
            schedule.put(day, new Event(starthour, startminet));
        } else {
            log.info("Записи не найдено!");
        }
    }
}
