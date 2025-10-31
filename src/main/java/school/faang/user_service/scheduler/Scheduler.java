package school.faang.user_service.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventService;

@Component
public class Scheduler {

    private final EventService eventService;

    public Scheduler(EventService eventService) {
        this.eventService = eventService;
    }

    @Scheduled(cron = "${cleanup.cron}")
    public void clearEvents() {
        eventService.clearPastEvents();
    }
}