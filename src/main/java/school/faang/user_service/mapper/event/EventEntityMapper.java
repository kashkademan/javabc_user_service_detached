package school.faang.user_service.mapper.event;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventCreateRequestDto;
import school.faang.user_service.dto.event.EventResponseDto;
import school.faang.user_service.dto.event.EventUpdateRequestDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.Rating;
import school.faang.user_service.entity.skill.Skill;
import school.faang.user_service.entity.user.User;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface EventEntityMapper {

    Event toEntityFromCreateDto(EventCreateRequestDto dto);

    @Mapping(source = "attendees", target = "attendeeIds", qualifiedByName = "attendeesToIds")
    @Mapping(source = "ratings", target = "ratingIds", qualifiedByName = "ratingsToIds")
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkillIds", qualifiedByName = "relatedSkillsToIds")
    EventResponseDto toDto(Event event);

    @Mapping(target = "relatedSkills", ignore = true)
    @Mapping(target = "attendees", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "maxAttendees", ignore = true)
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(EventUpdateRequestDto dto, @MappingTarget Event entity);

    List<EventResponseDto> toDtoList(List<Event> events);

    @Named("relatedSkillsToIds")
    default List<Long> skillsToIds(List<Skill> skills) {
        if (skills == null) {
            return new ArrayList<>();
        }
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }

    @Named("attendeesToIds")
    default List<Long> attendeesToIds(List<User> attendees) {
        if (attendees == null) {
            return new ArrayList<>();
        }
        return attendees.stream()
                .map(User::getId)
                .toList();
    }

    @Named("ratingsToIds")
    default List<Long> ratingsToIds(List<Rating> ratings) {
        if (ratings == null) {
            return new ArrayList<>();
        }
        return ratings.stream()
                .map(Rating::getId)
                .toList();
    }
}
