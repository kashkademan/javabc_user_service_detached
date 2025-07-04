package bjs283607;

public class Main {
    public static void main(String[] args) {
        ScheduleManager manager = new ScheduleManager();

        Schedule schedule = new Schedule("График");
        schedule.addSchedule("Понедельник", "09:00", "17:30");
        schedule.addSchedule("Вторник", "10:00", "18:30");

        manager.addSchedule(1, schedule);

        System.out.println(manager.getSchedule(1));

        schedule.updateSchedule("Понедельник", "08:30", "17:30");
        manager.updateSchedule(1, schedule);

        System.out.println(manager.getSchedule(1));
    }
}
