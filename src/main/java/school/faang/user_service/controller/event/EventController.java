package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.service.user.event.EventService;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RequestMapping("/events")
@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto create(@Valid @RequestBody CreateEventDto eventDto) {
        return eventService.create(eventDto);
    }

    @PutMapping("/{eventId}")
    public EventDto update(@PathVariable long eventId,
                           @Valid @RequestBody UpdateEventDto updateEventDto) throws AccessDeniedException {
        return eventService.update(eventId, updateEventDto);
    }

    @GetMapping
    public List<EventDto> getByFilters(@Valid EventFilterDto filters) {
        return eventService.getByFilters(filters);
    }

    @DeleteMapping("/{eventId}")
    public void delete(@PathVariable long eventId) throws AccessDeniedException {
        eventService.delete(eventId);
    }
}
