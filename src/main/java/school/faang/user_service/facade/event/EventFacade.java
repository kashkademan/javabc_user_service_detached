package school.faang.user_service.facade.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventCreateRequestDto;
import school.faang.user_service.dto.event.EventLiteResponseDto;
import school.faang.user_service.dto.event.EventResponseDto;
import school.faang.user_service.dto.event.EventFilterRequestDto;
import school.faang.user_service.dto.event.EventUpdateRequestDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.event.EventFilterMapper;
import school.faang.user_service.model.event.EventFilter;
import school.faang.user_service.service.event.EventService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventFacade {
    private final EventMapper eventMapper;
    private final EventFilterMapper eventFilterMapper;
    private final EventService eventService;

    public EventLiteResponseDto createEvent(EventCreateRequestDto dto) {
        Event event = eventMapper.toEntityFromCreateDto(dto);
        Event createdEvent = eventService.createEvent(event, dto.getRelatedSkillIds());

        return eventMapper.toEventLiteResponseDto(createdEvent);
    }

    public EventLiteResponseDto updateEvent(EventUpdateRequestDto dto) {
        Event existing = eventService.getEventById(dto.getId());
        eventMapper.updateEntityFromDto(dto, existing);
        Event updatedEvent = eventService.updateEventData(existing, dto.getRelatedSkills());

        return eventMapper.toEventLiteResponseDto(updatedEvent);
    }

    public EventResponseDto getEventById(long eventId) {
        return eventMapper.toEventResponseDto(eventService.getEventById(eventId));
    }

    public void deleteEventById(long eventId) {
        eventService.deleteEventById(eventId);
    }

    public List<EventResponseDto> getEventsByOwned(long userId) {
        List<Event> ownedEvents = eventService.getOwnedEvents(userId);
        return eventMapper.toEventResponseDtoList(ownedEvents);
    }

    public List<EventResponseDto> getParticipated(long userId) {
        List<Event> participatedEvents = eventService.getParticipatedEvents(userId);
        return eventMapper.toEventResponseDtoList(participatedEvents);
    }

    public List<EventLiteResponseDto> filter(EventFilterRequestDto filterDto) {
        EventFilter filter = eventFilterMapper.toFilter(filterDto);
        List<Event> events = eventService.getEventsByFilter(filter);
        return eventMapper.toEventLiteResponseDtoList(events);
    }
}
