package school.faang.user_service.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.messaging.event.EventStartEvent;
import school.faang.user_service.messaging.publisher.EventStartEventPublisher;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventStartScheduler {

    private final EventRepository eventRepository;
    private final UserService userService; //?
    private final EventStartEventPublisher eventStartEventPublisher;

    @Scheduled(cron = "${scheduler.event-start.cron}")
    public void publishStartEvents() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusMinutes(1);
        LocalDateTime to = now.plusMinutes(1);

        List<Event> startingEvents = eventRepository.findEventsStartingBetween(from, to);
        for (Event event : startingEvents) {
            try {
                List<Long> participantIds = event.getAttendees().stream()
                        .map(user -> user.getId())
                        .toList();

                EventStartEvent eventStartEvent = new EventStartEvent(String.valueOf(event.getId()), participantIds);

                eventStartEventPublisher.publish(eventStartEvent);
                log.info("Опубликовано EventStartEvent для события ID {}", event.getId());
            } catch (Exception e) {
                log.error("Не удалось обработать ID события {}: {}", event.getId(), e.getMessage(), e);
            }
        }
    }
}

