package school.faang.user_service.service;

import school.faang.user_service.dto.event.filter.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

public interface EventService {
    Event create(Event event, List<Long> eventSkillsIds, Long ownerId);

    Event updateEvent(Event event, List<Long> eventSkillsIds, Long ownerId, long id);

    Event getEvent(Long eventId);

    List<Event> getEventsByFilter(EventFilterDto filter);

    List<Event> getOwnedEvents(long userId);

    List<Event> getParticipatedEvents(long userId);

    String deleteEvent(long eventId);
}
