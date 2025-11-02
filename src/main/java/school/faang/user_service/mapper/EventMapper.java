package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.Skill;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = SPRING,
        unmappedTargetPolicy = IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    @Mapping(target = "relatedSkills", ignore = true)
    Event toEvent(CreateEventDto createEventDto);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "skillIds", source = "relatedSkills", qualifiedByName = "mapToSkillIds")
    EventDto toEventDto(Event event);

    @Mapping(target = "relatedSkills", ignore = true)
    void update(UpdateEventDto eventDto, @MappingTarget Event event);
    
    @Named("mapToSkillIds")
    default List<Long> mapToSkillIds(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }
}
