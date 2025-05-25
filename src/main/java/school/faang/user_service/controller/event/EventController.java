package school.faang.user_service.controller.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.RequestEventDto;
import school.faang.user_service.dto.event.ResponseEventDto;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    public ResponseEventDto create(@Valid RequestEventDto eventDto) {
        return eventService.create(eventDto);
    }

    public ResponseEventDto getEvent(long eventId) {
        return eventService.getEvent(eventId);
    }

    public List<ResponseEventDto> getEventsByFilter(EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    public void deleteEvent(long eventId) {
        eventService.deleteEvent(eventId);
    }

    public ResponseEventDto updateEvent(@Valid RequestEventDto eventDto) {
        return eventService.updateEvent(eventDto);
    }

    public List<ResponseEventDto> getOwnedEvents(long userId) {
        return eventService.getOwnedEvents(userId);
    }

    public List<ResponseEventDto> getParticipatedEvents(long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
