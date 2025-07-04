package school.faang.user_service.bjs283607;

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

