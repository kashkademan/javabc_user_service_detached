package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.event.*;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto create(@Valid @RequestBody CreateEventDto dto) {
        return eventService.create(dto);
    }

    @PutMapping("/{eventId}")
    public EventDto update(@PathVariable long eventId, @Valid @RequestBody UpdateEventDto dto) {
        return eventService.update(eventId, dto);
    }

    @GetMapping
    public List<EventDto> getByFilters(@Valid EventFilterDto filters) {
        return eventService.getByFilters(filters);
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long eventId) {
        eventService.delete(eventId);
    }
}
