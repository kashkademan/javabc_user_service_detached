package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validation.event.EventValidator;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final UserContext userContext;
    private final List<EventFilter> eventFilters;

    @Value("${scheduler.batch-size}")
    private int batchSize;
    @Value("${scheduler.time-out}")
    private int timeOut;

    @Override
    public EventDto create(CreateEventDto eventDto) {
        eventValidator.validateEventNotInPast(eventDto.startDate());
        eventValidator.validateEventDates(eventDto.startDate(), eventDto.endDate());

        Event event = eventMapper.toEvent(eventDto);
        Long currentUserId = userContext.getUserId();
        User owner = eventValidator.validateAndGetUser(currentUserId);

        event.setOwner(owner);
        event.setStatus(EventStatus.PLANNED);

        Event savedEvent = eventRepository.save(event);
        log.info("Event {} created", savedEvent.getId());
        return eventMapper.toEventDto(savedEvent);
    }

    @Override
    public EventDto update(long eventId, UpdateEventDto updateEventDto) {
        Event event = findEventById(eventId);

        eventValidator.validateEventOwnership(event);
        eventValidator.validateEventNotInPast(updateEventDto.startDate());
        eventValidator.validateEventDates(updateEventDto.startDate(), updateEventDto.endDate());

        eventMapper.update(updateEventDto, event);
        Event updatedEvent = eventRepository.save(event);
        log.info("Event {} updated", event.getId());
        return eventMapper.toEventDto(updatedEvent);
    }

    @Override
    public List<EventDto> getByFilters(EventFilterDto eventFilterDto) {
        Stream<Event> filteredEvents = eventRepository.findAll().stream();

        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter.isApplicable(eventFilterDto)) {
                filteredEvents = eventFilter.apply(filteredEvents, eventFilterDto);
            }
        }

        return filteredEvents
                .map(eventMapper::toEventDto)
                .toList();
    }

    @Override
    public void delete(long eventId) {
        Event event = findEventById(eventId);
        eventValidator.validateEventOwnership(event);

        eventRepository.delete(event);
        log.info("Event {} deleted", event.getId());
    }

    private Event findEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + eventId));
    }

    @Transactional
    public void clearPassedEvents() {
        List<Event> allEvents = eventRepository.findAll();
        List<Long> passedEventIds = allEvents.stream()
            .filter(event -> event.getEndDate().isBefore(LocalDateTime.now()))
            .map(Event::getId)
            .collect(Collectors.toList());

        if (passedEventIds.isEmpty()) {
            log.info("No passed events found to delete");
            return;
        } else {
            log.info("Found {} passed events to delete", passedEventIds.size());
        }

        List<List<Long>> batches = splitIntoBatches(passedEventIds, batchSize);

        ExecutorService executorService = Executors.newFixedThreadPool(batches.size());

        for (List<Long> batch : batches) {
            executorService.submit(() -> {
                eventRepository.deleteAllById(batch);
            });
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(timeOut, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private List<List<Long>> splitIntoBatches(List<Long> list, int batchSize) {
        return IntStream.range(0, (list.size() + batchSize - 1) / batchSize)
            .mapToObj(i -> list.subList(i * batchSize, Math.min((i + 1) * batchSize, list.size())))
            .collect(Collectors.toList());
    }
}