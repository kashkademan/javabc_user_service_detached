package school.faang.user_service.service.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final UserContext userContext;


    @Override
    public EventDto create(CreateEventDto eventDto) {
        Event event = eventMapper.toEvent(eventDto);
        Long currentUserId = userContext.getUserId();
        User owner = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + currentUserId));
        event.setOwner(owner);
        event.setStatus(EventStatus.PLANNED);

        Event savedEvent = eventRepository.save(event);
        log.info("User {} created", event.getId());
        return eventMapper.toEventDto(savedEvent);
    }

    @Override
    public EventDto update(long eventId, UpdateEventDto updateEventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
        Long currentUserId = userContext.getUserId();
        if (!event.getOwner().getId().equals(currentUserId)) {
            throw new ForbiddenException("You are not the owner of this event");
        }
        eventMapper.update(updateEventDto, event);

        Event updatedEvent = eventRepository.save(event);
        log.info("User {} updated", event.getId());
        return eventMapper.toEventDto(updatedEvent);
    }

    @Override
    public List<EventDto> getByFilters(EventFilterDto filters) {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .filter(event -> filters.titleContains() == null
                        || event.getTitle().toLowerCase().contains(filters.titleContains().toLowerCase()))
                .filter(event -> filters.descriptionContains() == null
                        || event.getDescription().toLowerCase().contains(filters.descriptionContains().toLowerCase()))
                .filter(event -> filters.ownerId() == null
                        || event.getOwner().getId().equals(filters.ownerId()))
                .filter(event -> filters.participantId() == null
                        || event.getAttendees().stream()
                                .anyMatch(user -> user.getId().equals(filters.participantId())))
                .filter(event -> filters.type() == null
                        || event.getType() == filters.type())
                .map(eventMapper::toEventDto)
                .toList();
    }

    @Override
    public void delete(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
        Long currentUserId = userContext.getUserId();

        if (!event.getOwner().getId().equals(currentUserId)) {
            throw new ForbiddenException("You are not the owner of this event");
        }

        eventRepository.delete(event);
        log.info("User {} deleted", event.getId());
    }
}