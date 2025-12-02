package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.events.EventCreateDto;
import school.faang.user_service.dto.events.EventResponseDto;
import school.faang.user_service.dto.events.EventStartDto;
import school.faang.user_service.dto.events.UpdateEventDto;
import school.faang.user_service.entity.EventStart;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    @Mapping(target = "skillIds", expression = "java(mapSkillToIds(event.getRelatedSkills()))")
    EventResponseDto toDto(Event event);

    @Mapping(target = "attendeesIds", expression = "java(mapAttendeesToIds(event.getAttendees()))")
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "eventStart", source = "eventStart")
    @Mapping(target = "title", source = "event.title")
    EventStartDto toStartDto(Event event, EventStart eventStart);

    @Mapping(target = "type", source = "eventType")
    Event toEntityCreate(EventCreateDto eventCreateDto);

    @Mapping(target = "title", source = "updateEventDto.title")
    @Mapping(target = "description", source = "updateEventDto.description")
    @Mapping(target = "startDate", source = "updateEventDto.startDate")
    @Mapping(target = "endDate", source = "updateEventDto.endDate")
    @Mapping(target = "maxAttendees", source = "updateEventDto.maxAttendees")
    @Mapping(target = "status", source = "updateEventDto.eventStatus")
    Event update(UpdateEventDto updateEventDto, Event event);

    default List<Long> mapSkillToIds(List<Skill> skillList) {
        return skillList.stream()
                .map(Skill::getId)
                .toList();
    }

    default List<Long> mapAttendeesToIds(List<User> users) {
        return users.stream()
                .map(User::getId)
                .toList();
    }
}