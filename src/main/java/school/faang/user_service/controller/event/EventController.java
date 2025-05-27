package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.service.EventService;

import java.util.List;

@Tag(name = "Event controller", description = "All the ways you can user events")
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Validated
public class EventController {
    private final EventService eventService;

    @Operation(summary = "Create a new event", description = "Create a new event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created successfully")
    })
    @PostMapping(value = {""})
    public EventDto create(@RequestBody @Valid EventDto event) {
        return eventService.create(event);
    }

    @Operation(summary = "Get event by id", description = "Get event by id")
    @GetMapping(value = "/{eventId}")
    public EventDto getEvent(@PathVariable("eventId") long eventId) {
        return eventService.getEvent(eventId);
    }

    @Operation(summary = "Get event by filter", description = "Get event by filter")
    @PostMapping(value = "/partial")
    public List<EventDto> getEventsByFilter(@RequestBody @Valid EventFilterDto eventFilterDto) {
        return eventService.getEventsByFilter(eventFilterDto);
    }

    @Operation(summary = "Delete event by id", description = "Delete event by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted successfully")
    })
    @DeleteMapping(value = "/{eventId}")
    public void deleteEvent(@PathVariable("eventId") long eventId) {
        eventService.deleteEvent(eventId);
    }

    @Operation(summary = "Update event", description = "Update event")
    @PatchMapping(value = "/{eventId}")
    public EventDto updateEvent(@RequestBody @Valid EventDto event) {
        return eventService.updateEvent(event);
    }

    @Operation(summary = "Get events for owner",
            description = "Get events for owner",
            parameters = {@Parameter(name = "userId", example = "1", description = "Owner ID", required = true)})
    @GetMapping(value = "/owner/{userId}")
    public List<EventDto> getOwnedEvents(@PathVariable("userId") long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @Operation(summary = "Get events for participant", description = "Get events for participant")
    @GetMapping(value = "/participant/{userId}")
    public List<EventDto> getParticipatedEvents(@PathVariable("userId") long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
