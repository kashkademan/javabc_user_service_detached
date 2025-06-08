package school.faang.user_service.facade.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventCreateRequestDto;
import school.faang.user_service.dto.event.EventResponseDto;
import school.faang.user_service.dto.event.EventFilterRequestDto;
import school.faang.user_service.dto.event.EventUpdateRequestDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventEntityMapper;
import school.faang.user_service.mapper.event.EventFilterMapper;
import school.faang.user_service.model.event.EventFilter;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventFacade {
    private final EventEntityMapper eventEntityMapper;
    private final EventFilterMapper eventFilterMapper;
    private final EventService eventService;

    public EventResponseDto create(EventCreateRequestDto dto) {
        Event event = eventEntityMapper.toEntityFromCreateDto(dto);
        Event createdEvent = eventService.create(event, dto.getRelatedSkillIds());

        return eventEntityMapper.toDto(createdEvent);
    }

    public EventResponseDto update(EventUpdateRequestDto dto) {
        Event existing = eventService.getEventById(dto.getId());
        eventEntityMapper.updateEntityFromDto(dto, existing);
        Event updatedEvent = eventService.updateEventData(existing, dto.getRelatedSkills());

        return eventEntityMapper.toDto(updatedEvent);
    }

    public EventResponseDto get(long id) {
        return eventEntityMapper.toDto(eventService.getEventById(id));
    }

    public void delete(long id) {
        eventService.deleteEventById(id);
    }

    public List<EventResponseDto> getOwned(long userId) {
        List<Event> ownedEvents = eventService.getOwnedEvents(userId);
        return eventEntityMapper.toDtoList(ownedEvents);
    }

    public List<EventResponseDto> getParticipated(long userId) {
        List<Event> participatedEvents = eventService.getOwnedEvents(userId);
        return eventEntityMapper.toDtoList(participatedEvents);
    }

    public List<EventResponseDto> filter(EventFilterRequestDto filterDto) {
        EventFilter filter = eventFilterMapper.toFilter(filterDto);
        List<Event> events = eventService.getEventsByFilter(filter);
        return eventEntityMapper.toDtoList(events);
    }
}
