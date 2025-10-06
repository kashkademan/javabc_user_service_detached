package school.faang.user_service.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public EventDto create(CreateEventDto eventDto) {
        validateCreateEventDto(eventDto);
        return eventService.create(eventDto);
    }

    public EventDto update(long eventId, UpdateEventDto updateEventDto) {
        return eventService.update(eventId, updateEventDto);
    }

    public List<EventDto> getByFilters(EventFilterDto filters) {
        return eventService.getByFilters(filters);
    }

    private void validateCreateEventDto(CreateEventDto createEventDto) {
        if (createEventDto == null) {
            throw new IllegalArgumentException("EventDto must not be null");
        }
        if (createEventDto.title() == null || createEventDto.title().trim().isEmpty()) {
            throw new IllegalArgumentException("Title must not be null or empty");
        }
        if (createEventDto.description() == null || createEventDto.description().trim().isEmpty()) {
            throw new IllegalArgumentException("Description must not be null or empty");
        }
        if (createEventDto.startDate() == null) {
            throw new IllegalArgumentException("Start date must not be null");
        }
        if (createEventDto.endDate() == null) {
            throw new IllegalArgumentException("End date must not be null");
        }
        if (createEventDto.type() == null) {
            throw new IllegalArgumentException("Event type must not be null");
        }
    }
}
