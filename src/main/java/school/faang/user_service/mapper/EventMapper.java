package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.User;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EventMapper {
    Event toEvent(CreateEventDto eventDto);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "attendees", target = "participantIds")
    EventDto toEventDto(Event event);

    void update(UpdateEventDto eventDto, @MappingTarget Event event);

    default Long map(User user) {
        return user == null ? null : user.getId();
    }
}
