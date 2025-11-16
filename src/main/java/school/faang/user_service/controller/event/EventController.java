package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.events.AllEventByFilterDto;
import school.faang.user_service.dto.events.EventCreateDto;
import school.faang.user_service.dto.events.EventResponseDto;
import school.faang.user_service.dto.events.UpdateEventDto;
import school.faang.user_service.service.events.EventServiceImpl;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/events")
@Validated
public class EventController {
    private final EventServiceImpl eventService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EventResponseDto createEvent(@RequestBody @Valid EventCreateDto eventCreateDto) {
        return eventService.createEvent(eventCreateDto);
    }

    @PutMapping("/{eventId}")
    public EventResponseDto updateEvent(
            @NotNull(message = "Event cannot be empty")
            @Positive(message = "Event cannot be negative")
            @PathVariable
            Long eventId,
            @Valid
            @RequestBody
            UpdateEventDto updateEventDto) {
        return eventService.updateEvent(eventId, updateEventDto);
    }

    @GetMapping
    public List<EventResponseDto> getAllByFilter(@RequestBody AllEventByFilterDto allEventByFilterDto,
                                                 @RequestParam
                                                 @DefaultValue(value = "0")
                                                 int page,
                                                 @RequestParam
                                                 @DefaultValue(value = "10")
                                                 int size) {
        return eventService.getAllByFilter(allEventByFilterDto, page, size);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{eventId}")
    public void deleteEvent(
            @NotNull(message = "Event cannot be empty")
            @Positive(message = "Event cannot be negative")
            @PathVariable
            Long eventId) {
        eventService.deleteEvent(eventId);
    }
}