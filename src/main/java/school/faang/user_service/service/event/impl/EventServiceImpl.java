package school.faang.user_service.service.event.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.filter.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.RecordNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.event.EventSpecification;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validation.event.EventValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static school.faang.user_service.validation.ValidationUtils.executeIfNotNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final EventValidation eventValidation;
    private final UserContext userContext;

    @Transactional
    @Override
    public Event create(Event event, List<Long> eventSkillsIds) {
        Long ownerId = userContext.getUserId();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RecordNotFoundException(
                        String.format("Пользователь с id %d не найден", ownerId)));

        eventValidation.validateUserHasAllEventSkills(eventSkillsIds, owner);

        List<Skill> eventSkills = skillRepository.findAllById(eventSkillsIds);
        event.setOwner(owner);
        event.setRelatedSkills(new ArrayList<>(eventSkills));
        log.info("Создание нового ивента: {}", event);
        return eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Event eventUpdates, List<Long> eventSkillsIds, long id) {
        long ownerId = userContext.getUserId();
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RecordNotFoundException(
                        String.format("Пользователь с id %d не найден", ownerId)));
        if (!Objects.equals(ownerId, owner.getId())) {
            throw new IllegalArgumentException("У вас нет прав на редактирование этого ивента");
        }
        Event event = eventRepository.findById(id).orElseThrow(() -> new RecordNotFoundException(
                String.format("Ивент с id %d не найден", id)));

        eventValidation.validateUserHasAllEventSkills(eventSkillsIds, owner);

        executeIfNotNull(eventUpdates.getTitle(), () -> event.setTitle(eventUpdates.getTitle()));
        executeIfNotNull(eventUpdates.getDescription(), () -> event.setDescription(eventUpdates.getDescription()));
        executeIfNotNull(eventUpdates.getStartDate(), () -> event.setStartDate(eventUpdates.getStartDate()));
        executeIfNotNull(eventUpdates.getEndDate(), () -> event.setEndDate(eventUpdates.getEndDate()));
        executeIfNotNull(eventUpdates.getLocation(), () -> event.setLocation(eventUpdates.getLocation()));
        executeIfNotNull(eventUpdates.getMaxAttendees(), () -> event.setMaxAttendees(eventUpdates.getMaxAttendees()));
        executeIfNotNull(eventUpdates.getRelatedSkills(), () -> {
            List<Skill> eventSkills = skillRepository.findAllById(eventSkillsIds);
            event.setRelatedSkills(new ArrayList<>(eventSkills));
        });
        executeIfNotNull(eventUpdates.getType(), () -> event.setType(eventUpdates.getType()));
        executeIfNotNull(eventUpdates.getStatus(), () -> event.setStatus(eventUpdates.getStatus()));

        log.info("Обновление ивента: {}", event);
        return eventRepository.save(event);
    }

    @Override
    public Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RecordNotFoundException(
                        String.format("Ивент с id %d не найден", eventId)
                ));
    }

    @Override
    public List<Event> getEventsByFilter(EventFilterDto filter) {
        Specification<Event> spec = EventSpecification.withFilter(filter);
        return eventRepository.findAll(spec);
    }

    @Override
    public List<Event> getOwnedEvents() {
        long ownerId = userContext.getUserId();
        log.info("Получен запрос на получение иваентов пользователя с ownerId: {}", ownerId);
        return eventRepository.findAllByUserId(ownerId);
    }

    @Override
    public List<Event> getParticipatedEvents() {
        long userId = userContext.getUserId();
        log.info("Получен запрос на получение иваентов, в которых участвует пользователь с userId: {}", userId);
        return eventRepository.findParticipatedEventsByUserId(userId);
    }

    @Override
    public String deleteEvent(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new RecordNotFoundException(String.format("Ивент с id %d не существует!", eventId));
        }
        eventRepository.deleteById(eventId);
        return String.format("Ивент с id %d удалён", eventId);
    }
}
