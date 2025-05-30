package school.faang.user_service.mapper.event;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventCreateDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.Rating;
import school.faang.user_service.entity.skill.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.model.event.EventFilter;
import school.faang.user_service.model.redis.promotion.EventRedisModel;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "relatedSkills", ignore = true)
    Event toEntityFromCreateDto(EventCreateDto dto);

    @Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "skillsToIds")
    @Mapping(source = "owner.id", target = "ownerId")
    EventDto toDto(Event event);

    @Mapping(target = "relatedSkills", ignore = true)
    @Mapping(target = "attendees", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "maxAttendees", ignore = true)
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(EventUpdateDto dto, @MappingTarget Event entity);

    List<EventDto> toDtoList(List<Event> events);

    EventFilter toFilter(EventFilterDto dto);


    // TODO: дописать поля для маппинга
    @Mapping(source = "id", target = "id", qualifiedByName = "longIdToStringId")
    @Mapping(source = "attendees", target = "attendeeIds", qualifiedByName = "attendeesToIds")
    @Mapping(source = "ratings", target = "ratingIds", qualifiedByName = "ratingsToIds")
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkillIds", qualifiedByName = "skillsToIds")
    EventRedisModel toEventRedis(Event event);

    // TODO: дописать поля для маппинга
    Event toEventEntity(EventRedisModel eventRedisModel);

    @Named("skillsToIds")
    default List<Long> skillsToIds(List<Skill> skills) {
        if (skills == null) {
            return new ArrayList<>();
        }
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }

    @Named("longIdToStringId")
    default String longIdToStringId(Long eventId) {
        return String.valueOf(eventId);
    }

    @Named("attendeesToIds")
    default List<Long> attendeesToIds(List<User> users) {
        if (users == null) {
            return new ArrayList<>();
        }
        return users.stream()
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
