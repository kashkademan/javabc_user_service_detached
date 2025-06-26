package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.RequestEventDto;
import school.faang.user_service.dto.event.ResponseEventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EventNotFoundException;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private static final String USER_VALIDATE_SKILLS = "The user %s cannot create an event '%s' because his " +
            "skills do not match the declared ones";
    private static final String EVENT_NOT_FOUND = "Event by ID=%d is not found";

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final SkillService skillService;
    private final List<Filter<EventFilterDto, Event>> eventFilters;

    @Transactional
    public ResponseEventDto create(RequestEventDto requestEventDto) {
        Event event = eventMapper.toEntity(requestEventDto);
        fillEvent(event, requestEventDto);

        validateUserSkills(requestEventDto, event);

        eventRepository.save(event);
        return eventMapper.toDto(event);
    }

    @Transactional
    public ResponseEventDto updateEvent(RequestEventDto requestEventDto) {
        Event eventFromDb = getEventById(requestEventDto.getId());

        eventMapper.update(eventFromDb, requestEventDto);
        fillEvent(eventFromDb, requestEventDto);

        validateUserSkills(requestEventDto, eventFromDb);

        eventRepository.save(eventFromDb);
        return eventMapper.toDto(eventFromDb);
    }

    @Transactional
    public ResponseEventDto getEvent(long eventId) {
        Event event = getEventById(eventId);
        return eventMapper.toDto(event);
    }

    public Event getEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(
                        String.format(EVENT_NOT_FOUND, eventId)));
    }

    @Transactional
    public List<ResponseEventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
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
    public List<ResponseEventDto> getOwnedEvents(long userId) {
        List<Event> allEvents = eventRepository.findAllByUserId(userId);
        return allEvents.stream().map(eventMapper::toDto).toList();
    }

    @Transactional
    public List<ResponseEventDto> getParticipatedEvents(long userId) {
        List<Event> allEvents = eventRepository.findParticipatedEventsByUserId(userId);
        return allEvents.stream().map(eventMapper::toDto).toList();
    }

    private void validateUserSkills(RequestEventDto requestEventDto, Event event) {
        User user = userService.getUserById(requestEventDto.getOwnerId());

        List<Long> userSkillsIdList = Optional.ofNullable(user.getSkills())
                .map(skills -> skills.stream().map(Skill::getId).toList())
                .orElseGet(ArrayList::new);

        List<Long> eventSkillIdList = Optional.ofNullable(event.getRelatedSkills())
                .map(skills -> skills.stream().map(Skill::getId).toList())
                .orElseGet(ArrayList::new);

        List<Long> differences = userSkillsIdList.stream()
                .filter(element -> !eventSkillIdList.contains(element))
                .toList();

        if (!differences.isEmpty()) {
            log.error(format(USER_VALIDATE_SKILLS, user.getUsername(), event.getTitle()));
            throw new DataValidationException(
                    format(USER_VALIDATE_SKILLS, user.getUsername(), event.getTitle()));
        }

        event.setOwner(user);
        event.setType(requestEventDto.getEventType());
        event.setStatus(requestEventDto.getEventStatus());
    }

    private void fillEvent(Event event, RequestEventDto requestEventDto) {
        User user = userService.getUserById(requestEventDto.getOwnerId());
        event.setOwner(user);
        List<Skill> skillList = skillService.getSkillsByIds(requestEventDto.getRelatedSkills());
        event.setRelatedSkills(skillList);
    }
}
