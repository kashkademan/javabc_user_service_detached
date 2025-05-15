package school.faang.user_service.facade.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.event.EventValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.model.event.EventFilter;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventFacade {

    private final UserContext userContext;
    private final EventMapper eventMapper;
    private final EventService eventService;
    private final UserService userService;
    private final SkillService skillService;

    public EventDto create(EventCreateDto dto) {
        long userId = userContext.getUserId();
        User owner = userService.getUserById(userId);
        Event entity = eventMapper.toEntityFromCreateDto(dto, owner);

        if (dto.getRelatedSkills() != null) {
            List<Skill> skills = skillService.getSkillsByIds(dto.getRelatedSkills());
            validateOwnerHasSkills(userContext.getUserId(), skills);
            entity.setRelatedSkills(skills);
        }

        return eventMapper.toDto(eventService.create(entity));
    }

    public EventDto update(EventUpdateDto dto) {
        Event existing = eventService.getEvent(dto.getId());

        if (!Objects.equals(userContext.getUserId(), existing.getOwner().getId())) {
            throw new EventValidationException("Обновление разрешено только автору события");
        }

        eventMapper.updateEntityFromDto(dto, existing);

        if (dto.getRelatedSkills() != null) {
            List<Skill> skills = skillService.getSkillsByIds(dto.getRelatedSkills());
            validateOwnerHasSkills(existing.getOwner().getId(), skills);
            existing.getRelatedSkills().clear();
            existing.getRelatedSkills().addAll(skills);
        }

        validateEventDates(existing.getStartDate(), existing.getEndDate());

        return eventMapper.toDto(eventService.updateEvent(existing));
    }

    public EventDto get(long id) {
        return eventMapper.toDto(eventService.getEvent(id));
    }

    public void delete(long id) {
        eventService.deleteEvent(id);
    }

    public List<EventDto> getOwned(long userId) {
        List<Event> ownedEvents = eventService.getOwnedEvents(userId);
        return eventMapper.toDtoList(ownedEvents);
    }

    public List<EventDto> getParticipated(long userId) {
        List<Event> participatedEvents = eventService.getOwnedEvents(userId);
        return eventMapper.toDtoList(participatedEvents);
    }

    public List<EventDto> filter(EventFilterDto filterDto) {
        EventFilter filter = eventMapper.toFilter(filterDto);
        List<Event> events = eventService.getEventsByFilter(filter);
        return eventMapper.toDtoList(events);
    }

    private void validateOwnerHasSkills(Long userId, List<Skill> skills) {
        Set<Long> userSkillIds = skillService.getSkillsByUserId(userId).stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        Set<Long> required = skills.stream().map(Skill::getId).collect(Collectors.toSet());

        if (!userSkillIds.containsAll(required)) {
            throw new EventValidationException("Пользователь не обладает всеми необходимыми навыками");
        }
    }

    private void validateEventDates(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new EventValidationException("Дата окончания события не может быть раньше даты начала");
        }
    }
}
