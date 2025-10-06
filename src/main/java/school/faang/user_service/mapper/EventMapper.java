package school.faang.user_service.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
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
import school.faang.user_service.repository.user.SkillRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {


    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Event toEvent(CreateEventDto eventDto, SkillRepository skillRepository);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "skills", source = "relatedSkills", qualifiedByName = "mapSkillTitles")
    EventDto toEventDto(Event event);

    @InheritConfiguration(name = "toEvent")
    void update(UpdateEventDto eventDto, @MappingTarget Event entity);

    @Named("mapSkillTitles")
    default Set<String> mapSkillTitles(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) return Set.of();
        return skills.stream()
                .map(Skill::getTitle)
                .collect(Collectors.toSet());
    }

    @AfterMapping
    default void mapSkills(CreateEventDto dto, @MappingTarget Event event, @Context SkillRepository skillRepository) {
        if (dto.skillsId() != null && !dto.skillsId().isEmpty()) {
            List<Skill> skills = skillRepository.findAllById(dto.skillsId());
            event.setRelatedSkills(skills);
        }
    }
}
