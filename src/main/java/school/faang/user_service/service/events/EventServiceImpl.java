package school.faang.user_service.service.events;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.events.AllEventByFilterDto;
import school.faang.user_service.dto.events.EventCreateDto;
import school.faang.user_service.dto.events.EventResponseDto;
import school.faang.user_service.dto.events.UpdateEventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.service.skill.SkillServiceImpl;
import school.faang.user_service.service.user.UserServiceImpl;

import java.util.List;

@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {
    private final SkillServiceImpl skillService;
    private final UserServiceImpl userService;
    private final UserContext userContext;

    @Override
    public EventCreateDto createEvent(EventCreateDto eventCreateDto) {
        User owner = userService.getById(userContext.getUserId());
        return null;
    }

    @Override
    public EventResponseDto updateEvent(UpdateEventDto updateEventDto) {
        return null;
    }

    @Override
    public List<EventResponseDto> getAllByFilter(AllEventByFilterDto allEventByFilterDto) {
        return List.of();
    }

    @Override
    public void deleteEvent(Long eventId) {
    }



    private void validateSkillAuthor(long ownerId, List<Long> skillList) {
        List<SkillDto> ownerSkills = skillService.getByUserId(ownerId);
        ownerSkills.forEach(skill -> {
            if (!skillList.contains(skill.id())) {
                throw new ForbiddenException("You cannot create or update an event without the proper skills!");
            }
        });

    }
}