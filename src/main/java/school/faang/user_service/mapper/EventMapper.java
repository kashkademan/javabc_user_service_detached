package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import school.faang.user_service.dto.event.request.EventRequest;
import school.faang.user_service.dto.event.response.EventResponse;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attendees", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "relatedSkills", ignore = true)
    @Mapping(target = "status", source = "eventRequest.eventStatus")
    @Mapping(target = "type", source = "eventRequest.eventType")
    @Mapping(target = "title", source = "eventRequest.title")
    @Mapping(target = "description", source = "eventRequest.description")
    @Mapping(target = "startDate", source = "eventRequest.startDate")
    @Mapping(target = "endDate", source = "eventRequest.endDate")
    @Mapping(target = "location", source = "eventRequest.location")
    @Mapping(target = "maxAttendees", source = "eventRequest.maxAttendees")
    Event eventRequestToEventEntity(EventRequest eventRequest);

    @Mapping(target = "ownerId", source = "event.owner.id")
    @Mapping(target = "eventStatus", source = "event.status")
    @Mapping(target = "eventType", source = "event.type")
    @Mapping(target = "relatedSkills", source = "event.relatedSkills", qualifiedByName = "toRelatedSkillsIds")
    EventResponse eventToEventResponse(Event event);

    @Named("toRelatedSkillsIds")
    default List<Long> skillsToSkillsIds(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }

    default List<EventResponse> toEventResponses(List<Event> events) {
        return events.stream()
                .map(this::eventToEventResponse)
                .toList();
    }
}
