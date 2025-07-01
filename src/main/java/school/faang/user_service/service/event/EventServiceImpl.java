package school.faang.user_service.service.event;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final UserContext userContext;

    @Override
    public EventDto create(CreateEventDto eventDto) {
        Event event = eventMapper.toEvent(eventDto);

        event.setOwner(userRepository.findById(userContext.getUserId())
                .orElseThrow(() -> new IllegalStateException("User not found")));

        validateOwnerSkills(event);

        event = eventRepository.save(event);
        log.info("Event created: {}", event.getId());
        return eventMapper.toEventDto(event);
    }

    @Override
    public EventDto update(long eventId, UpdateEventDto updateEventDto) {
        long requesterId = userContext.getUserId();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (event.getOwner().getId() != requesterId) {
            throw new ForbiddenException("User " + requesterId + " is not owner of event " + eventId);
        }

        eventMapper.update(updateEventDto, event);

        validateOwnerSkills(event);

        event = eventRepository.save(event);
        log.info("Event updated: {}", event.getId());
        return eventMapper.toEventDto(event);
    }

    @Override
    public List<EventDto> getByFilters(EventFilterDto filters) {
        List<Event> events = eventRepository.findAll();

        Stream<Event> stream = events.stream()
                .filter(event -> filters.getTitleContains() == null
                                 || event.getTitle().toLowerCase().contains(filters.getTitleContains().toLowerCase()))
                .filter(event -> filters.getDescriptionContains() == null
                                 || event.getDescription().toLowerCase()
                                         .contains(filters.getDescriptionContains().toLowerCase()))
                .filter(event -> filters.getOwnerId() == null
                                 || event.getOwner().getId().equals(filters.getOwnerId()))
                .filter(event -> filters.getParticipantId() == null
                                 || event.getAttendees().stream()
                                         .anyMatch(user -> user.getId().equals(filters.getParticipantId())))
                .filter(event -> filters.getType() == null
                                 || event.getType() == filters.getType());

        return stream.map(eventMapper::toEventDto).toList();
    }

    @Override
    public void delete(long eventId) {
        long requesterId = userContext.getUserId();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found"));

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
            throw new DataValidationException("Ownew does not have any skills");
        }
        boolean allSkillsPresent = eventSkills.stream()
                .allMatch(eventSkill -> ownerSkills.stream()
                        .anyMatch(ownerSkill -> ownerSkill.getId().equals(eventSkill.getId())));

        if (!allSkillsPresent) {
            throw new DataValidationException("Owner does not have all required skills for this event");
        }
    }
}

