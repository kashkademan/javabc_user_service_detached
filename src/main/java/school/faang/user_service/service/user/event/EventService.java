package school.faang.user_service.service.user.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final UserContext userContext;
    private final SkillRepository skillRepository;

    private static final String DEFAULT_LOCATION = "location";

    @Transactional
    public EventDto create(CreateEventDto createEventDto) {
        Event event = eventMapper.toEvent(createEventDto, skillRepository);
        Long ownerId = userContext.getUserId();
        User owner = userRepository.getByIdOrThrow(ownerId);
        event.setOwner(owner);
        event.setStatus(EventStatus.PLANNED);
        event.setCreatedAt(LocalDateTime.now());
        event.setLocation(DEFAULT_LOCATION);

        validateOwnerSkills(owner, createEventDto.skillsId());

        eventRepository.save(event);
        log.info("Event created with id: {}", event.getId());
        return eventMapper.toEventDto(event);
    }

    @Transactional
    public EventDto update(long eventId, UpdateEventDto updateEventDto) throws AccessDeniedException {
        Event event = eventRepository.getByIdOrThrow(eventId);
        checkOwner(event);
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


    @Transactional
    public void delete(long eventId) throws AccessDeniedException {
        Long currentUserId = userContext.getUserId();
        int deletedCount = eventRepository.deleteById(currentUserId, eventId);
        if (deletedCount == 0) {
            throw new EntityNotFoundException("Event not found or access denied");
        }
        log.info("Event deleted: {}", eventId);
    }

    private void checkOwner(Event event) throws AccessDeniedException {
        Long currentUserId = userContext.getUserId();
        if (!event.getOwner().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Only owner can modify event");
        }
    }

    private boolean matchesFilters(Event event, EventFilterDto filters) {
        if (filters == null) return true;

        return (filters.titleContains() == null || event.getTitle().contains(filters.titleContains())) &&
                (filters.descriptionContains() == null || event.getDescription()
                        .contains(filters.descriptionContains()))
                && (filters.ownerId() == null || event.getOwner().getId().equals(filters.ownerId())) &&
                (filters.type() == null || event.getType().equals(filters.type()));
    }

    private void validateOwnerSkills(User owner, Set<Long> skillsId) {
        if (skillsId == null || skillsId.isEmpty()) return;

        Set<Long> ownerSkillsId = owner.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        Set<Long> missingSkills = skillsId.stream()
                .filter(id -> !ownerSkillsId.contains(id))
                .collect(Collectors.toSet());

        if (!missingSkills.isEmpty()) {
            throw new DataValidationException("Owner does not have required skills: " + missingSkills);
        }
    }
}