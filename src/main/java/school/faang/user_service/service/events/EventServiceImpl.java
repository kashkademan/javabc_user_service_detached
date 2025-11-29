package school.faang.user_service.service.events;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.thread.ScheduleThreadsPoolConfig;
import school.faang.user_service.dto.events.AllEventByFilterDto;
import school.faang.user_service.dto.events.EventCreateDto;
import school.faang.user_service.dto.events.EventResponseDto;
import school.faang.user_service.dto.events.EventStartDto;
import school.faang.user_service.dto.events.UpdateEventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.EventStart;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.messages.redis.publishers.EventStartPublisher;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.skill.SkillServiceImpl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {
    private final SkillServiceImpl skillService;
    private final SkillRepository skillRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final UserContext userContext;
    private final EventMapper eventMapper;
    private final EntityManager entityManager;
    private final ScheduleThreadsPoolConfig taskScheduler;
    private final EventStartPublisher publishEventStart;
    @Value("${scheduler.clear-events.batch-size}")
    private int batchSize;
    @Value("${event-notification.one-day}")
    private int notificationOneDay;
    @Value("${event-notification.five-hours}")
    private int notificationFiveHours;
    @Value("${event-notification.one-hour}")
    private int notificationOneHour;
    @Value("${event-notification.ten-minutes}")
    private int notificationTenMinutes;

    @Transactional
    @Override
    public EventResponseDto createEvent(EventCreateDto eventCreateDto) {
        validateSkillAuthor(eventCreateDto.relatedSkillsId());
        User user = userRepository.getByIdOrThrow(userContext.getUserId());
        boolean existOwnerEvent = user.getOwnedEvents().stream()
                .anyMatch(event -> event.getTitle().equals(eventCreateDto.title())
                        && event.getOwner().getId() == userContext.getUserId());
        if (existOwnerEvent) {
            throw new ForbiddenException("You already have this event!");
        }
        List<Skill> skillEvent = skillRepository.findSkillByIds(eventCreateDto.relatedSkillsId());
        Event event = eventMapper.toEntityCreate(eventCreateDto);
        event.setRelatedSkills(skillEvent);
        event.setAttendees(new ArrayList<>());
        event.setStatus(EventStatus.PLANNED);
        event.setOwner(user);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDto(savedEvent);
    }

    @Override
    public EventResponseDto updateEvent(Long eventId, UpdateEventDto updateEventDto) {
        Event event = eventRepository.getByIdOrThrow(eventId);
        validateAuthorEvent(event);
        validateSkillAuthor(updateEventDto.relatedSkillsId());
        Event updateEvent = eventMapper.update(updateEventDto, event);
        List<Skill> skills = skillRepository.findSkillByIds(updateEventDto.relatedSkillsId());
        updateEvent.setRelatedSkills(skills);
        return eventMapper.toDto(eventRepository.save(updateEvent));
    }

    @Override
    public List<EventResponseDto> getAllByFilter(AllEventByFilterDto allEventByFilterDto, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "startDate"));
        String titleContains = allEventByFilterDto.titleContains();
        String descriptionContains = allEventByFilterDto.descriptionContains();
        EventType type = allEventByFilterDto.type();
        List<Event> events = eventRepository.findEventsByFilters(titleContains, descriptionContains, type,
                allEventByFilterDto.ownerId(),
                allEventByFilterDto.participantId(), pageable);
        return events.stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @Override
    public void deleteEvent(Long eventId) {
        Event event = eventRepository.getByIdOrThrow(eventId);
        validateAuthorEvent(event);
        eventRepository.delete(event);
    }

    @Override
    public EventResponseDto getEventById(Long eventId) {
        return eventMapper.toDto(eventRepository.getByIdOrThrow(eventId));
    }

    @Override
    public void prepareEventsToPublish() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Current Date {}", now);

        List<Event> events = eventRepository.findEventsFor24HourReminder();
        log.info("Number of Events per notification {}", events.size());
        events.forEach(event -> {
            LocalDateTime eventStartTime = event.getStartDate();
            scheduleNotificationEvent(eventMapper.toStartDto(event, EventStart.ONE_DAY),
                    eventStartTime.minusHours(notificationOneDay));
            scheduleNotificationEvent(eventMapper.toStartDto(event, EventStart.FIVE_HOURS),
                    eventStartTime.minusHours(notificationFiveHours));
            scheduleNotificationEvent(eventMapper.toStartDto(event, EventStart.ONE_HOUR),
                    eventStartTime.minusHours(notificationOneHour));
            scheduleNotificationEvent(eventMapper.toStartDto(event, EventStart.TEN_MINUTES),
                    eventStartTime.minusMinutes(notificationTenMinutes));
        });
    }

    private void scheduleNotificationEvent(EventStartDto eventStartDto, LocalDateTime notifyTime) {
        long delay = ChronoUnit.MILLIS.between(LocalDateTime.now(), notifyTime);
        if (delay <= 0) {
            log.info("Notification skipped (already past) for Event: {}, delay {}, date now {}",
                    eventStartDto.eventId(), delay, LocalDateTime.now(ZoneOffset.UTC));
            return;
        }
        log.info("{} Event waiting to send on {}", eventStartDto.eventId(), delay);
        taskScheduler.getTaskScheduler().schedule(()-> {
            publishEventStart.publish(eventStartDto);
            log.info("Event send to topic: {}", eventStartDto.eventId());
        }, Instant.now().plusMillis(delay));
    }

    @Override
    @Transactional
    public void clearExpiredEvents() {
        LocalDateTime cutoffDate = LocalDateTime.now();
        Integer deletedInBatch;
        int totalDeleted = 0;

        log.info("Starting cleanup of events older than {}", cutoffDate);

        do {
            deletedInBatch = eventRepository.deleteExpiredEventsBatch(cutoffDate, batchSize);
            totalDeleted += deletedInBatch;

            if (deletedInBatch > 0) {
                entityManager.flush();
                entityManager.clear();
            }

        } while (deletedInBatch >= batchSize);

        log.info("Cleanup finished. Total deleted: {} events", totalDeleted);
    }

    private void validateSkillAuthor(List<Long> skillList) {
        List<SkillDto> ownerSkills = skillService.getByUserId(userContext.getUserId());
        ownerSkills.forEach(skill -> {
            if (!skillList.contains(skill.id())) {
                throw new ForbiddenException("You cannot create or update an event without the proper skills!");
            }
        });
    }

    private void validateAuthorEvent(Event event) {
        if (event.getOwner().getId() != userContext.getUserId()) {
            throw new ForbiddenException("You are not owner for this event!");
        }
    }
}