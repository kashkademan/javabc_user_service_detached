package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.event.EventValidationException;
import school.faang.user_service.model.event.EventFilter;
import school.faang.user_service.model.redis.ActionType;
import school.faang.user_service.model.redis.TrackActionScore;
import school.faang.user_service.repository.event.EventFilterRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.event.EventValidator;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final UserService userService;
    private final SkillService skillService;
    private final EventRepository eventRepository;
    private final EventFilterRepository eventFilterRepository;
    private final EventValidator eventValidator;
    private final UserContext userContext;

    @Transactional
    public Event create(Event event, List<Long> relatedSkillIds) {
        long userId = userContext.getUserId();
        User owner = userService.getUserById(userId);
        event.setOwner(owner);

        if (relatedSkillIds != null && !relatedSkillIds.isEmpty()) {
            List<Skill> skills = skillService.getSkillsByIds(relatedSkillIds);
            eventValidator.validateOwnerHasSkills(userId, skills);
            event.setRelatedSkills(skills);
        }

        eventValidator.validateEventDates(event.getStartDate(), event.getEndDate());
        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public Event getEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventValidationException(
                        String.format("Событие с id=%d не найдено", eventId)));
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
    @TrackActionScore(ActionType.COMPLETE_EVENT)
    public Event updateEventData(Event event, List<Long> relatedSkillIds) {
        long userId = userContext.getUserId();
        if (!Objects.equals(userId, event.getOwner().getId())) {
            throw new EventValidationException("Обновление разрешено только автору события");
        }

        if (relatedSkillIds != null && !relatedSkillIds.isEmpty()) {
            List<Skill> skills = skillService.getSkillsByIds(relatedSkillIds);
            eventValidator.validateOwnerHasSkills(userId, skills);
            event.getRelatedSkills().clear();
            event.getRelatedSkills().addAll(skills);
        }

        eventValidator.validateEventDates(event.getStartDate(), event.getEndDate());
        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<Event> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Event> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsByFilter(EventFilter filter) {
        return eventFilterRepository.findByFilter(filter);
    }
}
