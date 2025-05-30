package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.facade.event.EventFacade;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventFacade eventFacade;

    @PostMapping
    public ResponseEntity<EventDto> create(@Valid @RequestBody EventCreateDto dto) {
        EventDto created = eventFacade.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping
    public ResponseEntity<EventDto> update(@Valid @RequestBody EventUpdateDto dto) {
        EventDto updated = eventFacade.update(dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> get(@PathVariable long eventId) {
        EventDto event = eventFacade.get(eventId);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> delete(@PathVariable long eventId) {
        eventFacade.delete(eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/owned/{userId}")
    public ResponseEntity<List<EventDto>> getOwnedEvents(@PathVariable long userId) {
        List<EventDto> events = eventFacade.getOwned(userId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/participated/{userId}")
    public ResponseEntity<List<EventDto>> getParticipatedEvents(@PathVariable long userId) {
        List<EventDto> events = eventFacade.getParticipated(userId);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/filter")
    public ResponseEntity<List<EventDto>> filter(@RequestBody EventFilterDto filter) {
        List<EventDto> events = eventFacade.filter(filter);
        return ResponseEntity.ok(events);
    }
}
