package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.controller.facade.event.EventFacade;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/events")
@RestController
@Validated
@Slf4j
public class EventController {

    private final EventFacade eventFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto create(@Valid @RequestBody CreateEventDto createEventDto) {
        log.info("Request to create event: {}", createEventDto.title());
        return eventFacade.create(createEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventDto update(@PathVariable long eventId,
                           @Valid @RequestBody UpdateEventDto updateEventDto) {
        log.info("Request to update event with id={}", eventId);
        return eventFacade.update(eventId, updateEventDto);
    }

    @PostMapping("/filters")
    public List<EventDto> getByFilters(@Valid @RequestBody EventFilterDto filters) {
        return eventFacade.filters(filters);
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long eventId) {
        log.info("Request to delete event with id={}", eventId);
        eventFacade.delete(eventId);
    }
}