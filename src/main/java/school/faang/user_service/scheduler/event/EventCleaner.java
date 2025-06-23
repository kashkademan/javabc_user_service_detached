package school.faang.user_service.scheduler.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventCleaner {

    private final EventRepository eventRepository;

    @Async("pastEventCleanExecutor")
    public void cleanEventsBatchAsync(List<Long> eventsIdBatch) {
        long start = System.currentTimeMillis();
        try {
            eventRepository.deleteAllByIdInBatch(eventsIdBatch);
        } catch (Exception ex) {
            log.error("Failed to delete batch", ex);
        }
        log.info("Batch deleted in {} millis.", (System.currentTimeMillis() - start));
    }
}