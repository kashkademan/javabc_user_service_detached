package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.impl.EventServiceImpl;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final EventServiceImpl eventService;

    @Scheduled(cron = "${scheduler.past-event-clean.cron}", zone = "${scheduler.past-event-clean.zone}")
    public void clearEvents() {
        long start = System.currentTimeMillis();
        log.info("Scheduled job started: clearing past events...");
        eventService.deletePastEvents();
        log.info("Scheduled job finished in {} millis: past events cleared.", (System.currentTimeMillis() - start));
    }
}