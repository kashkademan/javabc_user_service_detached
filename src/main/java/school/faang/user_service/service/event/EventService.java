package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
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
    private final UserContext userContext;

    public Event create(EventCreateDto eventCreateDto) {
        long ownerId = userContext.getUserId();
        User owner = userRepository.getByIdOrThrow(ownerId);

        EventValidator.validateEventCreation(eventCreateDto, owner);
        EventValidator.validateEventDates(eventCreateDto.startDate(), eventCreateDto.endDate());

        Event event = EventMapper.toEvent(eventCreateDto);
        event.setOwner(owner);
        event.setStatus(EventStatus.PLANNED);

        eventRepository.save(event);
        log.info("Event created: id={}, title='{}', ownerId={}, type={}, status={}",
                event.getId(),
                event.getTitle(),
                owner.getId(),
                event.getType(),
                event.getStatus());

        return event;
    }

    public Event update(long eventId, EventUpdateDto eventUpdateDto) {
        Event event = eventRepository.getByIdOrThrow(eventId);

        EventValidator.validateOwner(event, userContext.getUserId());
        EventValidator.validateEventUpdate(eventUpdateDto, event);

        EventMapper.updateEvent(eventUpdateDto, event);
        eventRepository.save(event);

        log.info("Event updated: id={}, title='{}', ownerId={}, type={}, status={}",
                event.getId(),
                event.getTitle(),
                event.getOwner().getId(),
                event.getType(),
                event.getStatus());

        return event;
    }

    public List<Event> getByFilters(EventFilterDto filters) {
        List<Event> allEvents = eventRepository.findAll();

        if (filters == null) {
            return allEvents;
        }

        return allEvents.stream()
                .filter(event -> matchesFilters(event, filters))
                .toList();
    }

    public void delete(long eventId) {
        long currentUserId = userContext.getUserId();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (!event.getOwner().getId().equals(currentUserId)) {
            throw new SecurityException("You don't have permission to delete this event");
        }

        int deletedCount = eventRepository.deleteById(eventId, currentUserId);

        if (deletedCount == 0) {
            log.warn("Failed to delete event: id={}, userId={} — not found or access denied",
                    eventId, currentUserId);
            throw new EntityNotFoundException("Event not found or access denied");
        }

        log.info("Event deleted: id={}, title='{}', deletedByUserId={}",
                eventId, event.getTitle(), currentUserId);
    }

    private boolean matchesFilters(Event event, EventFilterDto filters) {
        if (filters == null) {
            return true;
        }

        List<Predicate<Event>> predicates = new ArrayList<>();

        if (filters.titleContains() != null) {
            predicates.add(e -> e.getTitle() != null
                    && e.getTitle().contains(filters.titleContains()));
        }
        if (filters.descriptionContains() != null) {
            predicates.add(e -> e.getDescription() != null
                    && e.getDescription().contains(filters.descriptionContains()));
        }
        if (filters.ownerId() != null) {
            predicates.add(e -> e.getOwner() != null
                    && e.getOwner().getId() != null
                    && e.getOwner().getId().equals(filters.ownerId()));
        }
        if (filters.type() != null) {
            predicates.add(e -> e.getType() != null
                    && e.getType().equals(filters.type()));
        }

        return predicates.stream()
                .reduce(Predicate::and)
                .orElse(e -> true)
                .test(event);
    }
}