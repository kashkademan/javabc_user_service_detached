package school.faang.user_service.mapper;

import ch.qos.logback.core.model.ComponentModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.events.EventCreateDto;
import school.faang.user_service.dto.events.EventResponseDto;
import school.faang.user_service.dto.events.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.Skill;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    @Mapping(target = "skillIds", expression = "java(mapSkillToIds(relatedSkills))")
    EventResponseDto toDto(Event event);

    Event toEntityCreate(EventCreateDto eventCreateDto);

    Event update(UpdateEventDto updateEventDto, Event event);

    default List<Long> mapSkillToIds(List<Skill> skillList){
        return skillList.stream()
                .map(Skill::getId)
                .toList();
    }
}