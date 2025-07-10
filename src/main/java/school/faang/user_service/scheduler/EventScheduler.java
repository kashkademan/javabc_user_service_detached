package school.faang.user_service.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.EventService;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventScheduler {

    private final EventService eventService;
    private final Environment environment;

    @PostConstruct
    public  void init() {
        log.info("Event scheduler initialized with frequency: {}", environment.getProperty("event.removal.cron"));
    }

    @Scheduled(cron = "${event.removal.cron}")
    public void clearEvents() {
        int countEvents = eventService.clearEvents();
        log.info("Deleted {} past events", countEvents);
    }
}
