package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.filter.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.RecordNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.event.EventSpecification;
import school.faang.user_service.service.EventService;
import school.faang.user_service.validation.event.EventValidation;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final EventValidation eventValidation;

    @Transactional
    @Override
    public Event create(Event event, List<Long> eventSkillsIds, Long ownerId) {
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
    public Event updateEvent(Event event, List<Long> eventSkillsIds, Long ownerId, long id) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RecordNotFoundException(
                        String.format("Пользователь с id %d не найден", ownerId)));
        if (!eventRepository.existsById(id)) {
            throw new RecordNotFoundException(
                    String.format("Ивент с id %d не найден", id));
        }
        eventValidation.validateUserHasAllEventSkills(eventSkillsIds, owner);

        List<Skill> eventSkills = skillRepository.findAllById(eventSkillsIds);
        event.setId(id);
        event.setOwner(owner);
        event.setRelatedSkills(new ArrayList<>(eventSkills));
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
    public List<Event> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId);
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
