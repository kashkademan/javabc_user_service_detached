package school.faang.user_service.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.events.EventServiceImpl;

@RequiredArgsConstructor
@Component
public class ScheduledEventStart {
    private final EventServiceImpl eventService;

    @Scheduled(cron = "${schedule.events}")
    public void scheduledEventStart() {

    }
}