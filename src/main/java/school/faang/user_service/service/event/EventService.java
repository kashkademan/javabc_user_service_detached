package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.event.validation.EventValidation;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventService {

    private final EventMapper eventMapper;
    private final UserContext userContext;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final List<Filter<Event, EventFilterDto>> eventFilters;

    public Event create(Event eventToSave, List<Long> skillIds) {
        EventValidation.checkEventDates(eventToSave.getStartDate(), eventToSave.getEndDate());

        List<Skill> relatedSkills = skillRepository.findAllById(skillIds);
        eventToSave.setRelatedSkills(relatedSkills);

        long userId = userContext.getUserId();
        User user = userRepository.getByIdOrThrow(userId);

        EventValidation.checkOwnerSkills(relatedSkills, user.getSkills());

        eventToSave.setOwner(user);
        eventToSave.setStatus(EventStatus.PLANNED);

        Event savedEvent = eventRepository.save(eventToSave);
        log.info("The event has been created: {}", savedEvent.getId());
        return savedEvent;
    }


    public Event update(long eventId, UpdateEventDto updateEventDto) {
        Event existingEvent = eventRepository.getByIdOrThrow(eventId);

        EventValidation.checkEventOwner(existingEvent.getOwner().getId(), userContext.getUserId());

        eventMapper.update(updateEventDto, existingEvent);

        EventValidation.checkEventDates(existingEvent.getStartDate(), existingEvent.getEndDate());

        if (updateEventDto.skillIds() != null) {
            List<Skill> relatedSkills = skillRepository.findAllById(updateEventDto.skillIds());
            EventValidation.checkOwnerSkills(relatedSkills, existingEvent.getOwner().getSkills());
            existingEvent.setRelatedSkills(relatedSkills);
        }
        Event savedEvent = eventRepository.save(existingEvent);
        log.info("The event has been updated: {}", savedEvent.getId());
        return savedEvent;
    }

    public List<Event> getByFilters(EventFilterDto eventFilterDto) {
        Stream<Event> events = eventRepository.findAll().stream();

        for (Filter<Event, EventFilterDto> eventFilter : eventFilters) {
            if (eventFilter.isApplicable(eventFilterDto)) {
                events = eventFilter.apply(events, eventFilterDto);
            }
        }

        return events.toList();
    }

    public void delete(long eventId) {
        Event existingEvent = eventRepository.getByIdOrThrow(eventId);

        EventValidation.checkEventOwner(existingEvent.getOwner().getId(), userContext.getUserId());

        eventRepository.delete(existingEvent);
        log.info("The event has been deleted: {}", existingEvent.getId());
    }
}
