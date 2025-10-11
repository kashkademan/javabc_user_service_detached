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
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


@Slf4j
@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final UserContext userContext;

    private static final String DEFAULT_LOCATION = "location";

    public EventDto create(CreateEventDto createEventDto) {
        Long ownerId = userContext.getUserId();
        User owner = userRepository.getByIdOrThrow(ownerId);

        EventValidator.validateEventCreation(createEventDto, owner);

        Event event = eventMapper.toEvent(createEventDto);
        event.setOwner(owner);
        event.setStatus(EventStatus.PLANNED);
        event.setLocation(DEFAULT_LOCATION);

        eventRepository.save(event);
        log.info("Event created with id: {}", event.getId());
        return eventMapper.toEventDto(event);
    }

    public EventDto update(long eventId, UpdateEventDto updateEventDto) {
        Event event = eventRepository.getByIdOrThrow(eventId);
        EventValidator.validateOwner(event, userContext.getUserId());
        eventMapper.update(updateEventDto, event);
        eventRepository.save(event);
        log.info("Event updated: {}", eventId);
        return eventMapper.toEventDto(event);
    }


    public List<EventDto> getByFilters(EventFilterDto filters) {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .filter(event -> matchesFilters(event, filters))
                .map(eventMapper::toEventDto)
                .toList();
    }

    public void delete(long eventId) {
        Long currentUserId = userContext.getUserId();
        int deletedCount = eventRepository.deleteById(currentUserId, eventId);
        if (deletedCount == 0) {
            throw new EntityNotFoundException("Event not found or access denied");
        }
        log.info("Event deleted: {}", eventId);
    }

    private boolean matchesFilters(Event event, EventFilterDto filters) {
        if (filters == null) {
            return true;
        }

        List<Predicate<Event>> predicates = new ArrayList<>();

        if (filters.titleContains() != null) {
            predicates.add(e -> e.getTitle().contains(filters.titleContains()));
        }
        if (filters.descriptionContains() != null) {
            predicates.add(e -> e.getDescription().contains(filters.descriptionContains()));
        }
        if (filters.ownerId() != null) {
            predicates.add(e -> e.getOwner().getId().equals(filters.ownerId()));
        }
        if (filters.type() != null) {
            predicates.add(e -> e.getType().equals(filters.type()));
        }

        return predicates.stream()
                .reduce(Predicate::and)
                .orElse(e -> true)
                .test(event);
    }
}