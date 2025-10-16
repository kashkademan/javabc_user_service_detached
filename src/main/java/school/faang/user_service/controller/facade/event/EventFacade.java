package school.faang.user_service.controller.facade.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class EventFacade {

    private final EventService eventService;

    public EventDto create(EventCreateDto eventCreateDto) {
        Event event = eventService.create(eventCreateDto);
        return EventMapper.toEventDto(event);
    }

    public EventDto update(Long eventId, EventUpdateDto eventUpdateDto) {
        Event event = eventService.update(eventId, eventUpdateDto);
        return EventMapper.toEventDto(event);
    }

    public List<EventDto> filters(EventFilterDto filters) {
        List<Event> events = eventService.getByFilters(filters);
        return events.stream()
                .map(EventMapper::toEventDto)
                .toList();
    }

    public void delete(Long eventId) {
        eventService.delete(eventId);
    }
}