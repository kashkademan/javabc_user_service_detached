package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.execservice.AsyncExecutor;
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
    private final AsyncExecutor asyncExecutor;

    public void clearPastEvents(int chunkSize) {
        List<Long> completedEventIds = eventRepository.findAllIdByStatus(EventStatus.COMPLETED);
        if (completedEventIds.isEmpty()) {
            log.info("No completed events found.");
            return;
        }
        List<List<Long>> chunks = partitionList(completedEventIds, chunkSize);

        ThreadPoolTaskExecutor executor = asyncExecutor.getAsyncExecutor(chunks.size());

        chunks.forEach((chunk) -> executor.execute(() -> eventRepository.deleteAllById(chunk)));
    }

    private List<List<Long>> partitionList(List<Long> list, int chunkSize) {
        List<List<Long>> parts = new ArrayList<>((list.size() + chunkSize - 1) / chunkSize);
        for (int i = 0; i < list.size(); i += chunkSize) {
            parts.add(list.subList(i, Math.min(list.size(), i + chunkSize)));
        }
        return parts;
    }
}