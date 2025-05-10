package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.RecordNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.EventService;
import school.faang.user_service.validation.event.EventValidation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventValidation eventValidation;

    @Transactional
    @Override
    public Event create(Event event, List<Long> eventSkillsIds, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RecordNotFoundException(
                        String.format("Пользователь с id %d не найден", ownerId)));
        List<Skill> ownersSkills = owner.getSkills();
        Set<Long> ownersSkillsIds = new HashSet<>(
                ownersSkills.stream()
                        .map(Skill::getId)
                        .toList()
        );

        eventValidation.validateUserHasAllEventSkills(eventSkillsIds, ownersSkillsIds);

        event.setOwner(owner);
        event.setRelatedSkills(new ArrayList<>(ownersSkills));
        log.info("Создание нового ивента: {}", event);
        return eventRepository.save(event);
    }
}
