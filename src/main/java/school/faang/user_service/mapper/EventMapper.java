package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "ownerId" , source = "owner.id")
    @Mapping(target = "relatedSkills", expression = "java(getSkillIds(event))")
    @Mapping(target = "eventType", source = "type")
    @Mapping(target = "eventStatus", source = "status")
    EventDto toEventDto(Event event);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "relatedSkills", ignore = true)
    @Mapping(target = "type", source = "eventType")
    @Mapping(target = "status", source = "eventStatus")
    Event toEvent(EventDto eventDto);

    default List<Long> getSkillIds(Event event) {
        if (event.getRelatedSkills() == null) {
            return new ArrayList<Long>();
        }

        return event.getRelatedSkills().stream()
                .map(Skill::getId)
                .toList();
    }
}
