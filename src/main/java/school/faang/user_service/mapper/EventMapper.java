package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attendees", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "relatedSkills", source = "skills")
    @Mapping(target = "status", source = "eventDto.eventStatus")
    @Mapping(target = "type", source = "eventDto.eventType")
    @Mapping(target = "title", source = "eventDto.title")
    @Mapping(target = "description", source = "eventDto.description")
    @Mapping(target = "startDate", source = "eventDto.startDate")
    @Mapping(target = "endDate", source = "eventDto.endDate")
    @Mapping(target = "location", source = "eventDto.location")
    @Mapping(target = "maxAttendees", source = "eventDto.maxAttendees")
    Event toEventEntity(EventDto eventDto, User owner, List<Skill> skills);

    @Mapping(target = "ownerId", source = "event.owner.id")
    @Mapping(target = "eventStatus", source = "event.status")
    @Mapping(target = "eventType", source = "event.type")
    @Mapping(target = "relatedSkills", source = "skillsIds")
    EventDto toEventDto(Event event, List<Long> skillsIds);
}
