package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.redis.RedisTtlProperties;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.skill.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.exception.event.EventValidationException;
import school.faang.user_service.model.event.EventFilter;
import school.faang.user_service.repository.event.EventFilterRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.promotion.PromotionRedisService;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.utils.async.GracefullyShutdownThreadPool;
import school.faang.user_service.validation.event.EventValidator;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private static final int NUM_THREADS = 10;
    private final UserService userService;
    private final SkillService skillService;
    private final EventRepository eventRepository;
    private final EventFilterRepository eventFilterRepository;
    private final EventValidator eventValidator;
    private final UserContext userContext;
    private final EventRedisService eventRedisService;
    private final RedisTtlProperties redisTtlProperties;
    private final PromotionRedisService promotionRedisService;
    private final ApplicationContext applicationContext;

    @Transactional
    public Event createEvent(Event event, List<Long> relatedSkillIds) {
        long userId = userContext.getUserId();
        User owner = userService.getUserById(userId);
        event.setOwner(owner);
        event.setStatus(EventStatus.PLANNED);

        if (relatedSkillIds != null && !relatedSkillIds.isEmpty()) {
            List<Skill> skills = skillService.getSkillsByIds(relatedSkillIds);
            eventValidator.validateOwnerHasSkills(userId, skills);
            event.setRelatedSkills(skills);
        }

        eventValidator.validateEventDates(event.getStartDate(), event.getEndDate());
        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public Event getEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventValidationException(
                        String.format("Событие с id=%d не найдено", eventId)));
    }

    @Transactional(readOnly = true)
    public List<Event> getAllEvents(long eventId) {
        return eventRepository.findAll();
    }

    @Transactional
    public void deleteEventById(long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventValidationException(String.format("Событие с id=%d не найдено и не может быть удалено", eventId));
        }

        eventRepository.deleteById(eventId);
        log.info("Событие с id={} успешно удалено", eventId);
    }

    @Transactional
    public Event updateEventData(Event event, List<Long> relatedSkillIds) {
        long userId = userContext.getUserId();
        if (!Objects.equals(userId, event.getOwner().getId())) {
            throw new EventValidationException("Обновление разрешено только автору события");
        }

        if (relatedSkillIds != null && !relatedSkillIds.isEmpty()) {
            List<Skill> skills = skillService.getSkillsByIds(relatedSkillIds);
            eventValidator.validateOwnerHasSkills(userId, skills);
            event.getRelatedSkills().clear();
            event.getRelatedSkills().addAll(skills);
        }

        eventValidator.validateEventDates(event.getStartDate(), event.getEndDate());
        Event savedEvent = eventRepository.save(event);
        log.info("Event {} has been saved", savedEvent);

        eventRedisService.updatePromotedEvent(savedEvent);
        return savedEvent;
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
        List<Long> filteredEventIds = eventFilterRepository.findByFilter(filter);

        ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);

        try {
            List<CompletableFuture<Event>> futureEvents = filteredEventIds.stream()
                    .map(eventId -> CompletableFuture.supplyAsync(() ->
                            eventRedisService.getEventById(eventId)
                                    .orElseGet(() -> {
                                        EventService self = applicationContext.getBean(EventService.class);
                                        self.addEventInRedis(eventId);
                                        return getEventById(eventId);
                                    }), threadPool))
                    .toList();

            List<Event> events = futureEvents.stream()
                    .map(CompletableFuture::join)
                    .toList();

            promotionRedisService.decrementCountViewByEventIds(filteredEventIds);

            return events;

        } finally {
            GracefullyShutdownThreadPool.gracefullyShutdown(threadPool);
        }
    }

    @Async("addEventInRedisExecutor")
    public void addEventInRedis(long eventId) {
        Event event = getEventById(eventId);
        long ttl = redisTtlProperties.getEvent();
        eventRedisService.saveEvent(event, ttl);
    }
}
