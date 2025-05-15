package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.event.EventValidationException;
import school.faang.user_service.model.event.EventFilter;
import school.faang.user_service.repository.event.EventFilterRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventFilterRepository eventFilterRepository;

    @Transactional
    public Event create(Event event) {
        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public Event getEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventValidationException(
                        String.format("Событие с id=%d не найдено", eventId)));
    }

    @Transactional
    public void deleteEvent(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventValidationException(String.format("Событие с id=%d не найдено и не может быть удалено", eventId));
        }
        eventRepository.deleteById(eventId);
        log.info("Событие с id={} успешно удалено", eventId);
    }

    @Transactional
    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<Event> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Event> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsByFilter(EventFilter filter) {
        return eventFilterRepository.findByFilter(filter);
    }
}
