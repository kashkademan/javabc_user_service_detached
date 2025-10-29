package school.faang.user_service.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventService;

@Component
public class Scheduler {

    @Autowired
    private EventService eventService;

    @Scheduled(cron = "${scheduler.clear-events-cron}", zone = "Europe/Moscow")
    public void clearEvents() {
        eventService.clearPassedEvents();
    }
}