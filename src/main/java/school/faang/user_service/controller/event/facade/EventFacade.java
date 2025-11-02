package school.faang.user_service.controller.event.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventFacade {

    private final EventService eventService;
    private final EventMapper eventMapper;

    public EventDto create(CreateEventDto createEventDto) {
        Event eventToCreate = eventMapper.toEvent(createEventDto);
        Event createdEvent = eventService.create(eventToCreate, createEventDto.skillIds());
        return eventMapper.toEventDto(createdEvent);
    }

    public EventDto update(long eventId, UpdateEventDto updateEventDto) {
        Event updatedEvent = eventService.update(eventId, updateEventDto);
        return eventMapper.toEventDto(updatedEvent);
    }

    public List<EventDto> getByFilters(EventFilterDto filters) {
        List<Event> events = eventService.getByFilters(filters);
        return events.stream()
                .map(eventMapper::toEventDto)
                .toList();
    }

    public void delete(long eventId) {
        eventService.delete(eventId);
    }
}
