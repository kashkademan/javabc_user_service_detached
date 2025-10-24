package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.*;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final EventMapper mapper;
    private final UserContext userContext;

    @Override
    @Transactional
    public EventDto create(CreateEventDto dto) {
        long requesterId = userContext.getUserId();
        log.info("create event requested by userId={}", requesterId);

        User owner = userRepository.getByIdOrThrow(requesterId);

        validateDates(dto.startDate(), dto.endDate());
        List<Skill> eventSkills = loadSkills(dto.relatedSkillIds());
        ensureOwnerHasAllSkills(owner, eventSkills);

        Event event = mapper.toEvent(dto);
        event.setOwner(owner);
        event.setRelatedSkills(eventSkills);

        Event saved = eventRepository.save(event);
        log.info("event created id={} by userId={}", saved.getId(), requesterId);
        return mapper.toEventDto(saved);
    }

    @Override
    @Transactional
    public EventDto update(long eventId, UpdateEventDto dto) {
        long requesterId = userContext.getUserId();
        log.info("update event requested: eventId={}, requesterId={}", eventId, requesterId);

        Event event = eventRepository.getByIdOrThrow(eventId);
        ensureOwner(event, requesterId);
        validateDates(dto.startDate(), dto.endDate());
        mapper.update(event, dto);

        List<Skill> newSkills = loadSkills(dto.relatedSkillIds());
        ensureOwnerHasAllSkills(event.getOwner(), newSkills);
        event.setRelatedSkills(newSkills);

        Event saved = eventRepository.save(event);
        log.info("event updated id={} by userId={}", saved.getId(), requesterId);
        return mapper.toEventDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventDto> getByFilters(EventFilterDto f) {
        log.info("get events with filters: {}", f);
        List<Event> all = eventRepository.findAll();

        return all.stream()
                .filter(e -> f.titleContains() == null || containsIgnoreCase(e.getTitle(), f.titleContains()))
                .filter(e -> f.descriptionContains() == null || containsIgnoreCase(e.getDescription(), f.descriptionContains()))
                .filter(e -> f.type() == null || e.getType() == f.type())
                .filter(e -> f.ownerId() == null || (e.getOwner() != null && Objects.equals(e.getOwner().getId(), f.ownerId())))
                .filter(e -> {
                    if (f.participantId() == null) return true;
                    return e.getAttendees() != null && e.getAttendees().stream().anyMatch(u -> Objects.equals(u.getId(), f.participantId()));
                })
                .map(mapper::toEventDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(long eventId) {
        long requesterId = userContext.getUserId();
        log.info("delete event requested: eventId={}, requesterId={}", eventId, requesterId);

        Event event = eventRepository.getByIdOrThrow(eventId);
        ensureOwner(event, requesterId);
        eventRepository.delete(event);

        log.info("event deleted id={} by userId={}", eventId, requesterId);
    }

    private void ensureOwner(Event event, long requesterId) {
        long ownerId = event.getOwner() != null ? event.getOwner().getId() : -1L;
        if (ownerId != requesterId) {
            log.warn("requesterId doesn't match owner's : {}", requesterId);
            throw new ForbiddenException("Only owner can modify/delete the event.");
        }
    }

    private void validateDates(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new DataValidationException("endDate must be after startDate.");
        }
    }

    private boolean containsIgnoreCase(String text, String needle) {
        return text != null && needle != null && text.toLowerCase().contains(needle.toLowerCase());
    }

    private List<Skill> loadSkills(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        List<Skill> skills = skillRepository.findAllById(ids);
        if (skills.size() != new HashSet<>(ids).size()) {
            log.warn("Some skills not found by ids: {}", ids);
            throw new DataValidationException("Some skills not found by ids: " + ids);
        }
        return skills;
    }

    private void ensureOwnerHasAllSkills(User owner, List<Skill> eventSkills) {
        if (eventSkills.isEmpty()) return;
        Set<Long> ownerSkillIds = owner.getSkills() == null ? Set.of()
                : owner.getSkills().stream().map(Skill::getId).collect(Collectors.toSet());
        List<Long> missing = eventSkills.stream()
                .map(Skill::getId)
                .filter(id -> !ownerSkillIds.contains(id))
                .toList();
        if (!missing.isEmpty()) {
            log.warn("Owner lacks required skills: {}", missing);
            throw new DataValidationException("Owner lacks required skills: " + missing);
        }
    }
}
