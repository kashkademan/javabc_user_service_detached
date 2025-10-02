package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
@Slf4j
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @PostMapping
    EventDto create(@Valid @RequestBody CreateEventDto createEventDto) {
        log.info("Received a request to create an event: {}", createEventDto);
        Event eventToSave = eventMapper.toEvent(createEventDto);
        Event savedEvent = eventService.create(eventToSave, createEventDto.skillIds());
        return eventMapper.toEventDto(savedEvent);
    }

    @PatchMapping("/{eventId}")
    EventDto update(@PathVariable long eventId, @RequestBody UpdateEventDto updateEventDto) {
        log.info("Received a request to update an event. eventId: {}, {}", eventId, updateEventDto);
        Event updatedEvent = eventService.update(eventId, updateEventDto);
        return eventMapper.toEventDto(updatedEvent);
    }

    @GetMapping("/events-by-filters")
    List<EventDto> getByFilters(@RequestBody EventFilterDto filters) {
        log.info("Received a request to get events by filters: {}", filters);
        return eventService.getByFilters(filters);
    }

    @DeleteMapping("/{eventId}")
    void delete(@PathVariable long eventId) {
        log.info("Received a request to delete an event. eventId: {}", eventId);
        eventService.delete(eventId);
    }
}
