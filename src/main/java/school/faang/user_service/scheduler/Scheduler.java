package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.scheduler.event.EventCleanConfig;
import school.faang.user_service.scheduler.event.EventCleaner;
import school.faang.user_service.service.event.impl.EventServiceImpl;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final EventServiceImpl eventService;
    private final EventCleanConfig eventCleanConfig;
    private final EventCleaner eventCleaner;

    @Scheduled(cron = "${scheduler.past-event-clean.cron}")
    public void clearEvents() {
        long start = System.currentTimeMillis();
        int batchSize = eventCleanConfig.getBatchSize();
        int fetchLimit = eventCleanConfig.getFetchLimit();

        List<Long> pastEvents = eventService.getPastEventsId(fetchLimit);

        log.info("Scheduled job started: clearing {} past events...", pastEvents.size());

        ListUtils.partition(pastEvents, batchSize)
                .forEach(eventCleaner::cleanEventsBatch);

        log.info("Scheduled job finished in {} seconds: past events cleared.",
                (System.currentTimeMillis() - start)
        );
    }
}