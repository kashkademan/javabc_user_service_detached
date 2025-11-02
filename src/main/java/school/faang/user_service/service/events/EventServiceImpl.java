package school.faang.user_service.service.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.events.AllEventByFilterDto;
import school.faang.user_service.dto.events.EventCreateDto;
import school.faang.user_service.dto.events.EventResponseDto;
import school.faang.user_service.dto.events.UpdateEventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.skill.SkillServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
@Slf4j
public class EventServiceImpl implements EventService {
    private final SkillServiceImpl skillService;
    private final SkillRepository skillRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final UserContext userContext;
    private final EventMapper eventMapper;
    @Value("${scheduler.clear-events.batch-size}")
    private int batchSize;
    @Value("${scheduler.clear-events.threads}")
    private int countThreads;
    @Value("${scheduler.clear-events.await-termination-hours}")
    private int awaitTerminationHours;

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
    @Transactional
    public void clearExpiredEvents() {
        LocalDateTime now = LocalDateTime.now();
        ExecutorService executorService = Executors.newFixedThreadPool(countThreads);
        boolean hasMore = true;
        int pageNumber = 0;

        while(hasMore) {
            Page<Long> page = eventRepository.findExpiredEventIds(PageRequest.of(pageNumber, batchSize), now);
            List<Long> ids = page.getContent();
            hasMore = page.hasNext() && !ids.isEmpty();

            if (ids.isEmpty()) {
                break;
            }

            executorService.submit(() -> {
                eventRepository.deleteAllByIdInBatch(ids);
                log.info("Deleted batch of {} events", ids.size());
            });

            pageNumber++;
        }

        executorService.shutdown();

        try {
            executorService.awaitTermination(awaitTerminationHours, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Event cleanup interrupted", e);
        }

        log.info("Event cleanup finished");
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