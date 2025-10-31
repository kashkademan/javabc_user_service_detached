package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    @Value("${cleanup.chunk-size}")
    private int chunkSize;

    public void clearPastEvents() {
        List<Long> completedEventIds = eventRepository.findAll().stream()
                .filter(event -> event.getStatus().equals(EventStatus.COMPLETED))
                .map(Event::getId)
                .toList();
        if (completedEventIds.isEmpty()) {
            log.info("No completed events found.");
            return;
        }
        List<List<Long>> chunks = partitionList(completedEventIds, chunkSize);

        ExecutorService executor = Executors.newFixedThreadPool(chunks.size());

        chunks.forEach((chunk) -> executor.execute(() -> eventRepository.deleteAllById(chunk)));

        gracefulShutdown(executor);
    }

    private static <T> List<List<T>> partitionList(List<T> list, int chunkSize) {
        List<List<T>> parts = new ArrayList<>((list.size() + chunkSize - 1) / chunkSize);
        for (int i = 0; i < list.size(); i += chunkSize) {
            parts.add(list.subList(i, Math.min(list.size(), i + chunkSize)));
        }
        return parts;
    }

    private void gracefulShutdown(ExecutorService exec) {
        exec.shutdown();

        try {
            if (!exec.awaitTermination(10, TimeUnit.SECONDS)) {
                log.warn("Deletion task is taking too long ...");
                exec.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Deletion task has been interrupted ...");
            throw new RuntimeException(e);
        }
        log.info("Deletion task has been completed!");
        exec.shutdownNow();
    }
}