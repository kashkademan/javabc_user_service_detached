package school.faang.user_service.scheduler.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import school.faang.user_service.service.event.impl.EventServiceImpl;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventCleaner {

    private final EventServiceImpl eventService;

    @Async("pastEventCleanExecutor")
    public void cleanEventsBatch(List<Long> eventsIdBatch) {
        try {
            eventService.deletePastEventsBatch(eventsIdBatch);
        } catch (Exception ex) {
            log.error("Failed to delete batch", ex);
        }
    }
}