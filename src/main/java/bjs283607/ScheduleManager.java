package bjs283607;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;


@Slf4j
public class ScheduleManager {
    private Map<Integer, Schedule> schedules = new HashMap<>();

    public void addSchedule(int id, Schedule schedule) {
        schedules.put(id, schedule);
    }

    public String getSchedule(int id) {
        return schedules.get(id).toString();
    }

    public void updateSchedule(int id, Schedule newSchedule) {
        if (schedules.containsKey(id)) {
            schedules.put(id, newSchedule);
        } else {
            log.info("Записи не найдено!");
        }
    }
}

@ToString
@Slf4j
@EqualsAndHashCode
class Schedule {
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

class Event {
    private String start_time;
    private String stop_time;

    public Event(String starttime, String stoptime) {
        start_time = starttime;
        stop_time = stoptime;
    }

    @Override
    public String toString() {
        return "[" + start_time + " - " + stop_time + "]";
    }
}
