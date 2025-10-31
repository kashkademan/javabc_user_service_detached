package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.List;

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
            System.out.println("No completed events found.");
        }
        List<List<Long>> chunks = partitionList(completedEventIds, chunkSize);

        chunks.forEach(eventRepository::deleteAllById);
    }

    private static <T> List<List<T>> partitionList(List<T> list, int chunkSize) {
        List<List<T>> parts = new ArrayList<>((list.size() + chunkSize - 1) / chunkSize);
        for (int i = 0; i < list.size(); i += chunkSize) {
            parts.add(list.subList(i, Math.min(list.size(), i + chunkSize)));
        }
        return parts;
    }
}