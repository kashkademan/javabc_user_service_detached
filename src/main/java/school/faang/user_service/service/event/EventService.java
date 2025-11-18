package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ExecutorService executorService;

    @Value("${scheduler.batch-size}")
    private int batchSize;

    public void removePastEvents() {
        List<Event> expiredEvents = eventRepository.findByEventDateBefore(LocalDateTime.now());

        if (expiredEvents.isEmpty()) {
            log.info("No expired events found");
            return;
        }

        log.info("Found {} expired events to delete", expiredEvents.size());

        List<List<Long>> batches = partitionIntoBatches(
                expiredEvents.stream().map(Event::getId).toList(),
                batchSize
        );

        List<CompletableFuture<Void>> futures = batches.stream()
                .map(batch -> CompletableFuture.runAsync(
                        () -> eventRepository.deleteAllById(batch),
                        executorService
                ))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        log.info("Deletion process completed");
    }

    private List<List<Long>> partitionIntoBatches(List<Long> ids, int batchSize) {
        List<List<Long>> batches = new ArrayList<>();
        for (int i = 0; i < ids.size(); i += batchSize) {
            batches.add(ids.subList(i, Math.min(i + batchSize, ids.size())));
        }
        return batches;
    }
}