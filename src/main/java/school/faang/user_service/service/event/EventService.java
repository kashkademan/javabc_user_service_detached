package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.event.EventValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;

    @Transactional
    public Event create(Event event) {
        validateOwnerHasSkills(event.getOwner().getId(), event.getRelatedSkills());

        return eventRepository.save(event);
    }

    @Transactional(readOnly=true)
    public Event getEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventValidationException(String.format("Событие с id=%d не найдено", eventId)));
    }

    @Transactional
    public void deleteEvent(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventValidationException(String.format("Событие с id=%d не найдено и не может быть удалено", eventId));
        }

        eventRepository.deleteById(eventId);
        log.info("Событие с id={} успешно удалено", eventId);
    }

    @Transactional
    public Event updateEvent(Event event) {
        Event existingEvent = eventRepository.findById(event.getId())
                .orElseThrow(() -> new EventValidationException(String.format("Событие с id=%d не найдено", event.getId())));

        if (!Objects.equals(existingEvent.getOwner().getId(), event.getOwner().getId())) {
            log.warn("Попытка обновить событие пользователем, не являющимся автором");
            throw new EventValidationException("Обновление разрешено только автору события");
        }

        validateOwnerHasSkills(event.getOwner().getId(), event.getRelatedSkills());

        existingEvent.setTitle(event.getTitle());
        existingEvent.setDescription(event.getDescription());
        existingEvent.setStartDate(event.getStartDate());
        existingEvent.setEndDate(event.getEndDate());
        existingEvent.setLocation(event.getLocation());
        existingEvent.setMaxAttendees(event.getMaxAttendees());
        existingEvent.setType(event.getType());
        existingEvent.setStatus(event.getStatus());

        existingEvent.getRelatedSkills().clear();
        existingEvent.getRelatedSkills().addAll(event.getRelatedSkills());

        Event updated = eventRepository.save(existingEvent);
        log.info("Событие с id={} успешно обновлено", updated.getId());
        return updated;
    }

    @Transactional(readOnly=true)
    public List<Event> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId)
                .stream()
                .toList();
    }

    @Transactional(readOnly=true)
    public List<Event> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId)
                .stream()
                .toList();
    }

    @Transactional(readOnly=true)
    public List<Event> getEventsByFilter(EventFilterDto filter) {
        return eventRepository.findAll().stream()
                .filter(event -> filter.getTitle() == null || event.getTitle().toLowerCase().contains(filter.getTitle().toLowerCase()))
                .filter(event -> filter.getOwnerId() == null || Objects.equals(event.getOwner().getId(), filter.getOwnerId()))
                .filter(event -> filter.getEventType() == null || event.getType() == filter.getEventType())
                .filter(event -> filter.getEventStatus() == null || event.getStatus() == filter.getEventStatus())
                .filter(event -> filter.getStartFrom() == null || (event.getStartDate() != null && !event.getStartDate().isBefore(filter.getStartFrom())))
                .filter(event -> filter.getStartTo() == null || (event.getStartDate() != null && !event.getStartDate().isAfter(filter.getStartTo())))
                .toList();
    }

    private void validateOwnerHasSkills(long userId, List<Skill> skills) {
        Set<Long> userSkillIds = skillRepository.findAllByUserId(userId)
                .stream().map(Skill::getId).collect(Collectors.toSet());

        Set<Long> requiredSkillIds = skills.stream()
                .map(Skill::getId).collect(Collectors.toSet());

        if (!userSkillIds.containsAll(requiredSkillIds)) {
            throw new EventValidationException("Пользователь не обладает всеми необходимыми навыками");
        }
    }
}
