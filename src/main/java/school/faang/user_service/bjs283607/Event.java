package school.faang.user_service.bjs283607;

public class Event {
    private String startTime;
    private String stopTime;

    public Event(String startTime, String stopTime) {
        startTime = startTime;
        stopTime = stopTime;
    }

    @Override
    public String toString() {
        return "[" + startTime + " - " + stopTime + "]";
    }
}
