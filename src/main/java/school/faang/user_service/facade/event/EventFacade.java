package school.faang.user_service.facade.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.model.event.EventFilter;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventFacade {

    private final EventMapper eventMapper;
    private final EventService eventService;

    public EventDto create(EventCreateDto dto) {
        Event event = eventMapper.toEntityFromCreateDto(dto);
        Event createdEvent = eventService.create(event, dto.getRelatedSkills());

        return eventMapper.toDto(createdEvent);
    }

    public EventDto update(EventUpdateDto dto) {
        Event existing = eventService.getEvent(dto.getId());
        eventMapper.updateEntityFromDto(dto, existing);
        Event updatedEvent = eventService.updateEventData(existing, dto.getRelatedSkills());

        return eventMapper.toDto(updatedEvent);
    }

    public EventDto get(long id) {
        return eventMapper.toDto(eventService.getEvent(id));
    }

    public void delete(long id) {
        eventService.deleteEvent(id);
    }

    public List<EventDto> getOwned(long userId) {
        List<Event> ownedEvents = eventService.getOwnedEvents(userId);
        return eventMapper.toDtoList(ownedEvents);
    }

    public List<EventDto> getParticipated(long userId) {
        List<Event> participatedEvents = eventService.getOwnedEvents(userId);
        return eventMapper.toDtoList(participatedEvents);
    }

    public List<EventDto> filter(EventFilterDto filterDto) {
        EventFilter filter = eventMapper.toFilter(filterDto);
        List<Event> events = eventService.getEventsByFilter(filter);
        return eventMapper.toDtoList(events);
    }
}
