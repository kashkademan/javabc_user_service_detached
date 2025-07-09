package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class EventController {
    private final EventService eventService;

    public EventDto create(@Valid CreateEventDto eventDto) {
        return eventService.create(eventDto);
    }

    public EventDto update(long eventId, @Valid UpdateEventDto updateEventDto) {
        return eventService.update(eventId, updateEventDto);
    }

    public List<EventDto> getByFilters(EventFilterDto filters) {
        return eventService.getByFilters(filters);
    }

    public void delete(long eventId) {
        eventService.delete(eventId);
    }
}
