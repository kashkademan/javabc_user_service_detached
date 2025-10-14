package school.faang.user_service.controller.facade.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class EventFacade {

    private final EventService eventService;

    public EventDto create(CreateEventDto createEventDto) {
        return eventService.create(createEventDto);
    }

    public EventDto update(Long eventId, UpdateEventDto updateEventDto) {
        return eventService.update(eventId, updateEventDto);
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