package school.faang.user_service.service.events;

import school.faang.user_service.dto.events.AllEventByFilterDto;
import school.faang.user_service.dto.events.EventCreateDto;
import school.faang.user_service.dto.events.EventResponseDto;
import school.faang.user_service.dto.events.UpdateEventDto;

import java.util.List;

public interface EventService {

    EventResponseDto createEvent(EventCreateDto eventCreateDto);

    EventResponseDto updateEvent(Long eventId, UpdateEventDto updateEventDto);

    List<EventResponseDto> getAllByFilter(AllEventByFilterDto allEventByFilterDto, int limit, int offset);

    void deleteEvent(Long eventId);
}