package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final UserContext userContext;
    private final List<EventFilter> eventFilters;

    @Override
    public EventDto create(CreateEventDto eventDto) {
        Event event = eventMapper.toEvent(eventDto);
        User owner = userRepository.getByIdOrThrow(eventDto.ownerId());
        event.setOwner(owner);
        event = eventRepository.save(event);
        log.info("Event {} created", event.getId());
        return eventMapper.toEventDto(event);
    }

    @Override
    public EventDto update(long eventId, UpdateEventDto updateEventDto) {
        Event event = eventRepository.getByIdOrThrow(eventId);
        checkOwner(event);
        eventMapper.update(updateEventDto, event);
        event = eventRepository.save(event);
        log.info("Event {} updated", event.getId());
        return eventMapper.toEventDto(event);
    }

    @Override
    public List<EventDto> getByFilters(EventFilterDto filters) {
        Stream<Event> filteredUsers = eventRepository.findAll().stream();

        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter.isApplicable(filters)) {
                filteredUsers = eventFilter.apply(filteredUsers, filters);
            }
        }

        return filteredUsers.map(eventMapper::toEventDto).toList();
    }

    @Override
    public void delete(long eventId) {
        Event event = eventRepository.getByIdOrThrow(eventId);
        checkOwner(event);
        eventRepository.deleteById(eventId);
        log.info("Event {} successfully deleted", event.getId());
    }

    private void checkOwner(Event event) {
        Long currentUserId = userContext.getUserId();
        if (!Objects.equals(currentUserId, event.getOwner().getId())) {
            throw new ForbiddenException(
                    "User %d is not the owner of event %d".formatted(currentUserId, event.getId()));
        }
    }
}
