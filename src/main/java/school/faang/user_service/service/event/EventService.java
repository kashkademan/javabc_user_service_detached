package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EventNotFoundException;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private static final String USER_VALIDATE_SKILLS = "The user %s cannot create an event '%s' because his " +
            "skills do not match the declared ones";
    private static final String EVENT_NOT_FOUND = "Event not found";

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final List<Validator<EventDto>> eventValidator;
    private final List<Filter<EventFilterDto, Event>> eventFilters;

    @Transactional
    public EventDto create(EventDto eventDto) {
        validate(eventDto);

        Event event = eventMapper.toEntity(eventDto);

        validateUserSkills(eventDto, event);

        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    @Transactional
    public EventDto updateEvent(EventDto eventDto) {
        validate(eventDto);

        Event eventDb = eventRepository.findById(eventDto.getId())
                .orElseThrow(() -> new EventNotFoundException(EVENT_NOT_FOUND));

        Event event = eventMapper.toEntity(eventDto);
        event.setCreatedAt(eventDb.getCreatedAt());

        validateUserSkills(eventDto, event);

        event = eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    private void validate(EventDto eventDto) {
        for (Validator<EventDto> validator : eventValidator) {
            validator.validate(eventDto);
        }
    }

    private void validateUserSkills(EventDto eventDto, Event event) {
        User user = userService.getUserById(eventDto.getOwnerId());

        List<Long> userSkillsIdList;
        if (user.getSkills() == null) {
            userSkillsIdList = new ArrayList<>();
        } else {
            userSkillsIdList = user.getSkills().stream().map(Skill::getId).toList();
        }

        List<Long> eventSkillIdList;
        if (event.getRelatedSkills() == null) {
            eventSkillIdList = new ArrayList<>();
        } else {
            eventSkillIdList = event.getRelatedSkills().stream().map(Skill::getId).toList();
        }

        List<Long> differences = userSkillsIdList.stream()
                .filter(element -> !eventSkillIdList.contains(element))
                .toList();

        if (!differences.isEmpty()) {
            throw new DataValidationException(
                    String.format(USER_VALIDATE_SKILLS, user.getUsername(), event.getTitle()));
        }

        event.setOwner(user);
        event.setType(eventDto.getEventType());
        event.setStatus(eventDto.getEventStatus());
    }

    @Transactional
    public EventDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(EVENT_NOT_FOUND));
        return eventMapper.toDto(event);
    }

    @Transactional
    public List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        Stream<Event> allEvents = eventRepository.findAll().stream();

        for (var eventFilter : eventFilters) {
            if (eventFilter.isApplicable(eventFilterDto)) {
                allEvents = eventFilter.apply(allEvents, eventFilterDto);
            }
        }

        return allEvents.map(eventMapper::toDto).toList();
    }

    @Transactional
    public void deleteEvent(long eventId) {
        eventRepository.deleteById(eventId);
    }

    @Transactional
    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> allEvents = eventRepository.findAllByUserId(userId);
        return allEvents.stream().map(eventMapper::toDto).toList();
    }

    @Transactional
    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> allEvents = eventRepository.findParticipatedEventsByUserId(userId);
        return allEvents.stream().map(eventMapper::toDto).toList();
    }
}
