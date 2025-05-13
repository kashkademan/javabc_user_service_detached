package school.faang.user_service.controller.event;

import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.event.filter.EventFilterDto;
import school.faang.user_service.dto.event.request.EventRequest;
import school.faang.user_service.dto.event.response.EventResponseDto;

import java.util.List;

public interface EventController {
    ResponseEntity<EventResponseDto> create(EventRequest request);

    ResponseEntity<EventResponseDto> updateEvent(EventRequest request, long id);

    ResponseEntity<EventResponseDto> getEvent(long id);

    ResponseEntity<List<EventResponseDto>> getEventsByFilter(EventFilterDto filter);

    ResponseEntity<List<EventResponseDto>> getOwnedEvents();

    ResponseEntity<List<EventResponseDto>> getParticipatedEvents();

    ResponseEntity<String> deleteEvent(long id);
}
