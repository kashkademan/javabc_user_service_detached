package school.faang.user_service.service.event.scheduling;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TimeLeft {
    START(0),
    MINUTES_10(10),
    HOUR_1(60),
    HOURS_5(300),
    HOURS_24(1440),
    ;

    private final int minutes;
}
