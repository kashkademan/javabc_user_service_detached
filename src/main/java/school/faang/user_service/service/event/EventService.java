package school.faang.user_service.service.event;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStartEvent;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EventService {
    private EventRepository eventRepository;
    private EventStartEventPublisher eventStartEventPublisher;


    @Scheduled(cron = "${spring.task.scheduling.cron}")
    public void startEvent() {
        List<Event> events = eventRepository.findAll();

        for (Event event : events) {
            if (event.getStartDate().isEqual(LocalDateTime.now())) {
                List<Long> participantsIds = event.getAttendees().stream().map(User::getId).toList();
                eventStartEventPublisher.publish(new EventStartEvent(event.getId(), participantsIds));
            }
        }
    }
  
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;

import java.util.List;

public interface EventService {
    EventDto create(CreateEventDto eventDto);

    EventDto update(long eventId, UpdateEventDto updateEventDto);

    List<EventDto> getByFilters(EventFilterDto filters);

    void delete(long eventId);
}
