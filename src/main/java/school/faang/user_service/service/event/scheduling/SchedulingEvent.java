package school.faang.user_service.service.event.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchedulingEvent {

    private final SendEventService sendEventService;

    @Scheduled(fixedDelay = 60000)
    public void checkImminentEvents() {
        LocalDateTime dateNow = LocalDateTime.now();

        sendEventService.notifyEventsInMinute(dateNow, TimeLeft.START);
        sendEventService.notifyEventsInMinute(dateNow.plusMinutes(10), TimeLeft.MINUTES_10);
        sendEventService.notifyEventsInMinute(dateNow.plusHours(1), TimeLeft.HOUR_1);
        sendEventService.notifyEventsInMinute(dateNow.plusHours(5), TimeLeft.HOURS_5);
        sendEventService.notifyEventsInMinute(dateNow.plusDays(1), TimeLeft.HOURS_24);
    }


}
