package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validation.ValidationGroups;

import java.util.List;

@Validated
@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(
            @RequestBody @Validated(ValidationGroups.OnCreate.class) CreateEventDto eventDto) {
        return eventService.create(eventDto);
    }

    public EventDto update(
            @PathVariable long eventId,
            @RequestBody @Validated(ValidationGroups.OnUpdate.class) UpdateEventDto updateEventDto) {
        return eventService.update(eventId, updateEventDto);
    }

    public List<EventDto> getByFilters(@Valid EventFilterDto filters) {
        return eventService.getByFilters(filters);
    }
}
