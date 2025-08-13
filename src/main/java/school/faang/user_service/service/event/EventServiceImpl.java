package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.dto.event.EventViewDto;
import school.faang.user_service.dto.event.EventStartEvent;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.filter.FilterService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final UserContext userContext;
    private final FilterService<Event, EventFilterDto> filterService;
    private final EventStartEventPublisher eventStartEventPublisher;

    @Override
    @Transactional
    public EventViewDto create(EventCreateDto eventDto) {
        Event event = eventMapper.toEntity(eventDto);

        long userId = userContext.getUserId();
        User owner = userRepository.getByIdOrThrow(userId);
        event.setOwner(owner);

        log.info("Create event request by userId: {}", userId);

        validateOwnerSkills(event);

        event = eventRepository.save(event);
        log.info("Event created: {}", event.getId());

        eventStartEventPublisher.publish(new EventStartEvent(event.getTitle(), eventDto.getAttendeesIds()));
        return eventMapper.toViewDto(event);
    }

    @Override
    @Transactional
    public EventViewDto update(long eventId, EventUpdateDto eventUpdateDto) {
        long requesterId = userContext.getUserId();
        Event event = eventRepository.getByIdOrThrow(eventId);

        if (event.getOwner().getId() != requesterId) {
            throw new ForbiddenException("User " + requesterId + " is not owner of event " + eventId);
        }

        eventMapper.update(eventUpdateDto, event);

        validateOwnerSkills(event);

        event = eventRepository.save(event);
        log.info("Event updated: {}", event.getId());
        return eventMapper.toViewDto(event);
    }

    @Override
    @Transactional
    public List<EventViewDto> getList(EventFilterDto dto) {
        List<Event> events = eventRepository.findAll();
        events = filterService.getFilteredList(events, dto);
        return events.stream()
                .map(eventMapper::toViewDto)
                .toList();
    }

    @Override
    @Transactional
    public void delete(long eventId) {
        long requesterId = userContext.getUserId();

        Event event = eventRepository.getByIdOrThrow(eventId);

        if (event.getOwner().getId() != requesterId) {
            throw new ForbiddenException("User " + requesterId + " is not owner of event " + eventId);
        }

        eventRepository.delete(event);
        log.info("Event deleted: {}", event.getId());
    }

    public void validateOwnerSkills(Event event) {
        List<Skill> eventSkills = event.getRelatedSkills();
        if (eventSkills == null || eventSkills.isEmpty()) {
            return;
        }
        List<Skill> ownerSkills = event.getOwner().getSkills();
        if (ownerSkills == null) {
            throw new DataValidationException("Owner does not have any skills");
        }
        boolean allSkillsPresent = eventSkills.stream()
                .allMatch(eventSkill -> ownerSkills.stream()
                        .anyMatch(ownerSkill -> ownerSkill.getId().equals(eventSkill.getId())));

        if (!allSkillsPresent) {
            throw new DataValidationException("Owner does not have all required skills for this event");
        }
    }
}

