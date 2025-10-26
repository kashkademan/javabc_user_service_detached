package school.faang.user_service.service.events;

import lombok.RequiredArgsConstructor;
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
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.service.skill.SkillServiceImpl;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {
    private final SkillServiceImpl skillService;
    private final SkillRepository skillRepository;
    private final EventRepository eventRepository;
    private final UserContext userContext;
    private final EventMapper eventMapper;

    @Transactional
    @Override
    public EventResponseDto createEvent(EventCreateDto eventCreateDto) {
        validateSkillAuthor(eventCreateDto.relatedSkillsId());
        List<Skill> skillEvent = skillRepository.findSkillByIds(eventCreateDto.relatedSkillsId());
        Event event = eventMapper.toEntityCreate(eventCreateDto);
        event.setRelatedSkills(skillEvent);
        event.setAttendees(new ArrayList<>());
        return eventMapper.toDto(event);
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