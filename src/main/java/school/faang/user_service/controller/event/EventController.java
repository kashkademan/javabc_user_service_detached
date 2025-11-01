package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.controller.event.facade.EventFacade;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
@RestController
public class EventController {

    private final EventFacade eventFacade;

    @PostMapping
    EventDto create(@Valid @RequestBody CreateEventDto createEventDto) {
        return eventFacade.create(createEventDto);
    }

    @PatchMapping("/{eventId}")
    EventDto update(@PathVariable long eventId, @RequestBody UpdateEventDto updateEventDto) {
        return eventFacade.update(eventId, updateEventDto);
    }

    @GetMapping("/events-by-filters")
    List<EventDto> getByFilters(@RequestBody EventFilterDto filters) {
        return eventFacade.getByFilters(filters);
    }

    @DeleteMapping("/{eventId}")
    void delete(@PathVariable long eventId) {
        eventFacade.delete(eventId);
    }
}
