package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "relatedSkills", expression = "java(mappingRelatedSkills(event))")
    @Mapping(target = "ownerId", expression = "java(mappingOwnerId(event))")
    @Mapping(target = "eventType", source = "type")
    @Mapping(target = "eventStatus", source = "status")
    EventDto toDto(Event event);

    @Mapping(target = "relatedSkills", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "attendees", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Event toEntity(EventDto eventDto);

    default List<Long> mappingRelatedSkills(Event event) {
        if (event.getRelatedSkills() != null) {
            return event.getRelatedSkills().stream().map(Skill::getId).toList();
        }

        return new ArrayList<>();
    }

    default Long mappingOwnerId(Event event) {
        Long ownerId = Long.MIN_VALUE;
        if (event.getOwner() != null) {
            ownerId = event.getOwner().getId();
        }

        return ownerId;
    }
}
