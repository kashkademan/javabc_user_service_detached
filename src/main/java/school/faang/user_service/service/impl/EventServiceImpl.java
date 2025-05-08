package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.EventCreationNotAllowedException;
import school.faang.user_service.exception.RecordNotFoundException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.EventService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventDto create(EventDto eventDto) {
        User owner = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new RecordNotFoundException(
                        String.format("Пользователь с id %d не найден", eventDto.getOwnerId())));
        List<Skill> ownersSkills = owner.getSkills();
        Set<Long> ownersSkillsIds = new HashSet<>(
                ownersSkills.stream()
                        .map(Skill::getId)
                        .toList()
        );
        List<Long> eventSkillsIds = eventDto.getRelatedSkills();

        if (eventSkillsIds != null && !eventSkillsIds.isEmpty()) {
            Set<Long> requiredSkillsIds = new HashSet<>(eventSkillsIds);
            requiredSkillsIds.removeAll(ownersSkillsIds);

            if (!requiredSkillsIds.isEmpty()) {
                throw new EventCreationNotAllowedException(
                        String.format("Недостаточно навыков для создания данного мероприятия. Отсутствуют навыки: %s",
                                requiredSkillsIds)
                );
            }
        }

        Event event = eventMapper.toEventEntity(eventDto, owner, ownersSkills);
        log.info("Creating new event: {}", event);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toEventDto(savedEvent, ownersSkills.stream()
                .map(Skill::getId)
                .toList());
    }
}
