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
import school.faang.user_service.messages.redis.publishers.PublishEventStart;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.skill.SkillServiceImpl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    @Value("${scheduler.clear-events.batch-size}")
    private int batchSize;
    private final PublishEventStart publishEventStart;

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
    public void startEventsPublish() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        log.info("Current Date {}", now);

        LocalDateTime windowStart = now.plusHours(23);
        log.info("windowStart Date {}", windowStart);
        LocalDateTime windowEnd = now.plusHours(25);
        log.info("windowEnd Date {}", windowEnd);
        List<Event> events = eventRepository.findEventsFor24HourReminder();
        log.info("Number of Events per notification {}", events.size());
        events.forEach(event -> {
            LocalDateTime eventStartTime = event.getStartDate();
            scheduleNotification(eventMapper.toStartDto(event, EventStart.ONE_DAY),
                    eventStartTime.minusHours(24));
            scheduleNotification(eventMapper.toStartDto(event, EventStart.FIVE_HOURS),
                    eventStartTime.minusHours(5));
            scheduleNotification(eventMapper.toStartDto(event, EventStart.ONE_HOUR),
                    eventStartTime.minusHours(1));
            scheduleNotification(eventMapper.toStartDto(event, EventStart.TEN_MINUTES),
                    eventStartTime.minusMinutes(10));
        });
    }

    private void scheduleNotification(EventStartDto eventStartDto, LocalDateTime notifyTime) {
        LocalDateTime now = LocalDateTime.now();
        if (notifyTime.isBefore(now)) {
            log.info("Notification skipped (already past) for Event: {}", eventStartDto.eventId());
            return;
        }

        long delay = Duration.between(now, notifyTime).toMillis();

     /*   Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                publishEventStart.sendNotification(eventStartDto);
            }
        }, delay);*/
        publishEventStart.sendNotification(eventStartDto);
        log.info("Event send to topic: {}", eventStartDto.eventId());
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