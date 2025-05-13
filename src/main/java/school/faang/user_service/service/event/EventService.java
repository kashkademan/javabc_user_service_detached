package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.filter.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

public interface EventService {
    Event create(Event event, List<Long> eventSkillsIds, Long ownerId);

    Event updateEvent(Event event, List<Long> eventSkillsIds, long id);

    Event getEvent(Long eventId);

    List<Event> getEventsByFilter(EventFilterDto filter);

    List<Event> getOwnedEvents();

    List<Event> getParticipatedEvents();

    String deleteEvent(long eventId);
}
