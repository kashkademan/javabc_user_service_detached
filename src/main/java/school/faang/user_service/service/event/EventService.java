package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Slf4j
public class EventService {

    private final EventMapper eventMapper;
    private final UserContext userContext;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final List<Filter<Event, EventFilterDto>> eventFilters;

    public Event create(Event eventToSave, List<Long> skillIds) {
        checkEventDates(eventToSave.getStartDate(), eventToSave.getEndDate());

        List<Skill> relatedSkills = skillRepository.findAllById(skillIds);
        eventToSave.setRelatedSkills(relatedSkills);

        long userId = userContext.getUserId();
        User user = userRepository.getByIdOrThrow(userId);

        checkOwnerSkills(relatedSkills, user.getSkills());

        eventToSave.setOwner(user);
        eventToSave.setStatus(EventStatus.PLANNED);

        Event savedEvent = eventRepository.save(eventToSave);
        log.info("The event has been created: {}", savedEvent.getId());
        return savedEvent;
    }


    public Event update(long eventId, UpdateEventDto updateEventDto) {
        Event existingEvent = eventRepository.getByIdOrThrow(eventId);

        checkEventOwner(existingEvent.getOwner().getId());

        eventMapper.update(updateEventDto, existingEvent);

        checkEventDates(existingEvent.getStartDate(), existingEvent.getEndDate());

        if (updateEventDto.skillIds() != null) {
            List<Skill> relatedSkills = skillRepository.findAllById(updateEventDto.skillIds());
            checkOwnerSkills(relatedSkills, existingEvent.getOwner().getSkills());
            existingEvent.setRelatedSkills(relatedSkills);
        }
        Event savedEvent = eventRepository.save(existingEvent);
        log.info("The event has been updated: {}", savedEvent.getId());
        return savedEvent;
    }

    public List<EventDto> getByFilters(EventFilterDto eventFilterDto) {
        Stream<Event> events = eventRepository.findAll().stream();

        for (Filter<Event, EventFilterDto> eventFilter : eventFilters) {
            if (eventFilter.isApplicable(eventFilterDto)) {
                events = eventFilter.apply(events, eventFilterDto);
            }
        }

        return events
                .map(eventMapper::toEventDto)
                .toList();
    }

    public void delete(long eventId) {
        Event existingEvent = eventRepository.getByIdOrThrow(eventId);

        checkEventOwner(existingEvent.getOwner().getId());

        eventRepository.delete(existingEvent);
        log.info("The event has been deleted: {}", existingEvent.getId());
    }

    private void checkEventOwner(long eventOwnerId) {
        long requesterId = userContext.getUserId();
        if (!Objects.equals(requesterId, eventOwnerId)) {
            throw new ForbiddenException(String.format("User %d doesn't match event owner!", requesterId));
        }
    }

    private void checkEventDates(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime now = LocalDateTime.now();
        StringBuilder stringBuilder = new StringBuilder();
        boolean exceptionMustBeThrown = false;
        if (startDate.isBefore(now)) {
            stringBuilder.append("The start date must be no earlier than the current date.");
            exceptionMustBeThrown = true;
        }
        if (endDate.isBefore(startDate)) {
            stringBuilder.append("The start date must be earlier than the end date.");
            exceptionMustBeThrown = true;
        }
        if (exceptionMustBeThrown) {
            throw new DataValidationException(stringBuilder.toString());
        }
    }

    private void checkOwnerSkills(List<Skill> requiredSkills, List<Skill> userSkills) {
        if (!userSkills.containsAll(requiredSkills)) {
            throw new DataValidationException("The owner does not have the necessary skills");
        }
    }
}
