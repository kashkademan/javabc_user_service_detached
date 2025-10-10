package school.faang.user_service.mapper;


import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.CreateEventDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.user.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    Event toEvent(CreateEventDto eventDto);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "skills", source = "relatedSkills", qualifiedByName = "mapSkillTitles")
    EventDto toEventDto(Event event);

    @InheritConfiguration(name = "toEvent")
    void update(UpdateEventDto eventDto, @MappingTarget Event entity);

    @Named("mapSkillTitles")
    default Set<String> mapSkillTitles(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return Set.of();
        }
        return skills.stream()
                .map(Skill::getTitle)
                .collect(Collectors.toSet());
    }

    default List<Skill> mapSkills(Set<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return new ArrayList<>();
        }
        return skillIds.stream()
                .map(id -> {
                    Skill skill = new Skill();
                    skill.setId(id);
                    return skill;
                })
                .collect(Collectors.toList());
    }
}