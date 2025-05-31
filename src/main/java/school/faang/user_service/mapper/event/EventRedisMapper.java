package school.faang.user_service.mapper.event;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.Rating;
import school.faang.user_service.entity.skill.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.model.redis.event.EventRedisModel;
import school.faang.user_service.utils.redis.RedisKeyUtil;

import java.util.ArrayList;
import java.util.List;

import static school.faang.user_service.model.redis.RedisHashType.EVENT;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface EventRedisMapper {

    @Mapping(source = "id", target = "id", qualifiedByName = "idToRedisKey")
    @Mapping(source = "attendees", target = "attendeeIds", qualifiedByName = "attendeesToIds")
    @Mapping(source = "ratings", target = "ratingIds", qualifiedByName = "ratingsToIds")
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkillIds", qualifiedByName = "relatedSkillsToIds")
    EventRedisModel toEventRedis(Event event);

    @Mapping(source = "id", target = "id", qualifiedByName = "redisKeyToId")
    @Mapping(source = "attendeeIds", target = "attendees", qualifiedByName = "idsToAttendees")
    @Mapping(source = "ratingIds", target = "ratings", qualifiedByName = "idsToRatings")
    @Mapping(source = "ownerId", target = "owner", qualifiedByName = "idToOwner")
    @Mapping(source = "relatedSkillIds", target = "relatedSkills", qualifiedByName = "idsToRelatedSkills")
    Event toEventEntity(EventRedisModel eventRedisModel);

    @Named("idToRedisKey")
    default String idToRedisKey(Long eventId) {
        return RedisKeyUtil.generateKey(eventId, EVENT);
    }

    @Named("redisKeyToId")
    default Long redisKeyToId(String eventId) {
        return RedisKeyUtil.extractId(eventId);
    }

    @Named("relatedSkillsToIds")
    default List<Long> skillsToIds(List<Skill> skills) {
        if (skills == null) {
            return new ArrayList<>();
        }
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }

    @Named("idsToRelatedSkills")
    default List<Skill> idsToSkills(List<Long> relatedSkillIds) {
        if (relatedSkillIds == null) {
            return new ArrayList<>();
        }
        return relatedSkillIds.stream()
                .map(id -> {
                    Skill skill = new Skill();
                    skill.setId(id);
                    return skill;
                })
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

    @Named("idsToAttendees")
    default List<User> idsToAttendees(List<Long> attendeeIds) {
        if (attendeeIds == null) {
            return new ArrayList<>();
        }
        return attendeeIds.stream()
                .map(id -> {
                    User user = new User();
                    user.setId(id);
                    return user;
                })
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

    @Named("idsToRatings")
    default List<Rating> idsToRatings(List<Long> ratingIds) {
        if (ratingIds == null) {
            return new ArrayList<>();
        }
        return ratingIds.stream()
                .map(id -> {
                    Rating rating = new Rating();
                    rating.setId(id);
                    return rating;
                })
                .toList();
    }

    @Named("idToOwner")
    default User idToOwner(Long ownerId) {
        if (ownerId == null) {
            return null;
        }
        User owner = new User();
        owner.setId(ownerId);
        return owner;
    }
}
