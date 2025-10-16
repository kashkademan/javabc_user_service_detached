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
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validation.EventValidator;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final UserContext userContext;
    private final List<EventFilter> eventFilters;

    @Override
    public EventDto create(CreateEventDto eventDto) {
        eventValidator.validateEventNotInPast(eventDto.startDate());
        eventValidator.validateEventDates(eventDto.startDate(), eventDto.endDate());

        Event event = eventMapper.toEvent(eventDto);
        Long currentUserId = userContext.getUserId();
        User owner = eventValidator.validateAndGetUser(currentUserId);

        event.setOwner(owner);
        event.setStatus(EventStatus.PLANNED);

        Event savedEvent = eventRepository.save(event);
        log.info("Event {} created", savedEvent.getId());
        return eventMapper.toEventDto(savedEvent);
    }

    @Override
    public EventDto update(long eventId, UpdateEventDto updateEventDto) {
        Event event = findEventById(eventId);

        eventValidator.validateEventOwnership(event);
        eventValidator.validateEventNotInPast(updateEventDto.startDate());
        eventValidator.validateEventDates(updateEventDto.startDate(), updateEventDto.endDate());

        eventMapper.update(updateEventDto, event);
        Event updatedEvent = eventRepository.save(event);
        log.info("Event {} updated", event.getId());
        return eventMapper.toEventDto(updatedEvent);
    }

    @Override
    public List<EventDto> getByFilters(EventFilterDto eventFilterDto) {
        Stream<Event> filteredEvents = eventRepository.findAll().stream();

        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter.isApplicable(eventFilterDto)) {
                filteredEvents = eventFilter.apply(filteredEvents, eventFilterDto);
            }
        }

        return filteredEvents
                .map(eventMapper::toEventDto)
                .toList();
    }

    @Override
    public void delete(long eventId) {
        Event event = findEventById(eventId);
        eventValidator.validateEventOwnership(event);

        eventRepository.delete(event);
        log.info("Event {} deleted", event.getId());
    }

    private Event findEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
    }
}