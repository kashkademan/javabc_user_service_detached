package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.events.EventServiceImpl;

@RequiredArgsConstructor
@Component
public class Scheduler {

    private final EventServiceImpl eventService;

    @Scheduled(cron = "${scheduler.clear-events.cron}")
    public void clearEvents() {
        eventService.clearExpiredEvents();
    }
}
