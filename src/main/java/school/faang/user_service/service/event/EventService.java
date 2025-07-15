package school.faang.user_service.service.event;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ExecutorService executor;

    @Value("${event.removal.batch-size}")
    private int batchSize;

    public int clearEvents() {
        List<Event> allEvents = eventRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        List<Long> pastEventIds = allEvents.stream()
                .filter(event -> event.getEndDate().isBefore(now))
                .map(Event::getId)
                .toList();

        if (pastEventIds.isEmpty()) {
            log.info("No past events found for deletion.");
            return 0;
        }

        List<List<Long>> batches = partitionList(pastEventIds, batchSize);
        List<Callable<Void>> tasks = new ArrayList<>();

        for (List<Long> batch : batches) {
            tasks.add(() -> {
                eventRepository.deleteByIds(batch);
                log.info("Deleted batch ({} items): {}", batch.size(), batch);
                return null;
            });
        }

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Event deletion interrupted", e);
        }

        return pastEventIds.size();
    }

    private List<List<Long>> partitionList(List<Long> list, int size) {
        List<List<Long>> parts = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            parts.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return parts;
    }

    @PreDestroy
    public void shutdownExecutor() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("Executor did not terminate in time, forcing shutdown.");
                executor.shutdownNow();
            } else {
                log.info("ExecutorService shut down successfully.");
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            log.error("Executor shutdown interrupted", e);
        }
    }
}
