package school.faang.user_service.service.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStartEvent;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class EventService {
    EventRepository eventRepository;
    EventStartEventPublisher eventStartEventPublisher;

    @Autowired
    public EventService(EventRepository eventRepository, EventStartEventPublisher eventStartEventPublisher) {
        this.eventRepository = eventRepository;
        this.eventStartEventPublisher = eventStartEventPublisher;
    }

    @Scheduled(cron = "0 * * * * ?")
    public void startEvent() {
        System.out.println("Hello World!");
        List<Event> events = eventRepository.findAll();

        for (Event event : events) {
            if (event.getStartDate().isEqual(LocalDateTime.now())) {
                List<Long> participantsIds = event.getAttendees().stream().map(User::getId).toList();
                eventStartEventPublisher.publish(new EventStartEvent(event.getId(), participantsIds));
            }
        }
    }
}
