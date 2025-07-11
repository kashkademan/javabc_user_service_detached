package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/api/v1/events")
public class EventController {
    private final EventService eventService;

    @PostMapping
    public EventDto create(@Valid @RequestBody CreateEventDto eventDto) {
        return eventService.create(eventDto);
    }

    @PutMapping("/{eventId}")
    public EventDto update(@PathVariable long eventId, @Valid @RequestBody UpdateEventDto updateEventDto) {
        return eventService.update(eventId, updateEventDto);
    }

    @GetMapping
    public List<EventDto> getByFilters(EventFilterDto filters) {
        return eventService.getByFilters(filters);
    }

    @DeleteMapping("/{eventId}")
    public void delete(@PathVariable long eventId) {
        eventService.delete(eventId);
    }
}
